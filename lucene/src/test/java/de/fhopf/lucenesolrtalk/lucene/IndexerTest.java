package de.fhopf.lucenesolrtalk.lucene;

import de.fhopf.lucenesolrtalk.Talk;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;

/**
 */
public class IndexerTest {

    private Directory directory;
    private Indexer indexer;

    @Before
    public void initIndexer() {
        directory = new RAMDirectory();
        indexer = new Indexer(directory);
    }

    @Test
    public void twoTalksAreIndexed() throws IOException {
        Talk talk1 = new Talk("/path/to/talk1", "Title 1", Arrays.asList("Author 1"), new Date(), "Contents", new ArrayList<String>(), "");
        Talk talk2 = new Talk("/path/to/talk2", "Title 2", Arrays.asList("Author 2"), new Date(), "More Contents", new ArrayList<String>(), "");

        indexer.index(talk1, talk2);
        assertDocumentCount(2);
    }

    private void assertDocumentCount(int amount) throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        assertEquals(amount, reader.numDocs());
        reader.close();
    }

    @Test
    public void indexOneTalkFromDirectory() throws IOException {
        Path dir = Files.createTempDirectory("indexertest");
        Path file = Files.createTempFile(dir, "post", ".properties");
        List<String> lines = new ArrayList<>();
        lines.add("speaker=Tester");
        lines.add("title=Test");
        lines.add("content=Content");
        lines.add("date=12.06.2012");
        lines.add("categories=Test1,Test2");
        Files.write(file, lines, Charset.forName("ISO-8859-1"));

        indexer.indexDirectory(dir.toAbsolutePath().toString());

        assertDocumentCount(1);
    }

    @Test
    public void transactionsAreNotIsolated() throws IOException, InterruptedException {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, new KeywordAnalyzer());
        final IndexWriter writer = new IndexWriter(directory, config);
        // add a dummy document so the index is created
        Document dummy = new Document();
        dummy.add(new TextField("name", "value", Field.Store.NO));
        writer.addDocument(dummy);
        writer.commit();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc1 = new Document();
                doc1.add(new TextField("key", "doc1", Field.Store.NO));
                try {
                    writer.addDocument(doc1);
                    // no commit
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t1.join();

        // no result as there was no commit
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
        TopDocs docs = searcher.search(new TermQuery(new Term("key", "doc1")), 10);
        assertEquals(0, docs.totalHits);

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc2 = new Document();
                doc2.add(new TextField("key", "doc2", Field.Store.NO));
                try {
                    writer.addDocument(doc2);
                    writer.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t2.start();
        t2.join();

        // both documents are committed
        searcher = new IndexSearcher(DirectoryReader.open(directory));
        docs = searcher.search(new TermQuery(new Term("key", "doc1")), 10);
        assertEquals(1, docs.totalHits);
        docs = searcher.search(new TermQuery(new Term("key", "doc2")), 10);
        assertEquals(1, docs.totalHits);
    }


}

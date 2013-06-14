package de.fhopf.lucenesolrtalk.lucene;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class IndexingAndSearchingTest {

    @Test
    public void indexAndSearch() throws IOException, ParseException {

        Directory directory = indexDocument();
        
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(Version.LUCENE_43, "title", new GermanAnalyzer(Version.LUCENE_43));
        Query query = parser.parse("suchen");
        TopDocs topDocs = searcher.search(query, 5);

        assertResults(topDocs, searcher);
        
    }
    
    @Test
    public void searchSorted() throws IOException, ParseException {
        Directory directory = indexDocument();
        
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(Version.LUCENE_43, "title", new GermanAnalyzer(Version.LUCENE_43));
        Query query = parser.parse("suchen");
        Sort sortByDate = new Sort(new SortField("date", SortField.Type.DOC));
        TopDocs topDocs = searcher.search(query, 5, sortByDate);

        assertResults(topDocs, searcher);
    }

    private Directory indexDocument() throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", "Such Evolution", Field.Store.YES));
        doc.add(new TextField("speaker", "Florian Hopf", Field.Store.YES));
        doc.add(new StringField("date", "20130704", Field.Store.YES));
        Directory directory = FSDirectory.open(new File("/tmp/talk-index"));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, new GermanAnalyzer(Version.LUCENE_43));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            writer.addDocument(doc);
            writer.commit();
        }
        return directory;
    }

    private void assertResults(TopDocs topDocs, IndexSearcher searcher) throws IOException {
        assertEquals(1, topDocs.totalHits);
        
        for (ScoreDoc scoreDoc: topDocs.scoreDocs) {
            Document result = searcher.doc(scoreDoc.doc);
            assertEquals("Florian Hopf", result.get("speaker"));
        }
    }
}

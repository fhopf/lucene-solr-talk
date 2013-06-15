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
        Document doc1 = new Document();
        doc1.add(new TextField("title", "suche suche", Field.Store.YES));
        doc1.add(new StringField("date", "19830101", Field.Store.NO));
        Document doc2 = new Document();
        doc2.add(new TextField("title", "suche banane", Field.Store.YES));
        doc2.add(new StringField("date", "20130101", Field.Store.NO));
        
        Directory directory = indexDocument(doc1, doc2);
        
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(Version.LUCENE_43, "title", new GermanAnalyzer(Version.LUCENE_43));
        Query query = parser.parse("suchen");
        Sort sortByDate = new Sort(new SortField("date", SortField.Type.STRING, true));
        TopDocs topDocs = searcher.search(query, 5, sortByDate);

        assertEquals(topDocs.totalHits, 2);
        Document firstResult = searcher.doc(topDocs.scoreDocs[0].doc);
        Document secondResult = searcher.doc(topDocs.scoreDocs[1].doc);
        
        assertEquals("suche banane", firstResult.get("title"));
        assertEquals("suche suche", secondResult.get("title"));
        
        // search again without sort
        topDocs = searcher.search(query, 5);

        assertEquals(topDocs.totalHits, 2);
        firstResult = searcher.doc(topDocs.scoreDocs[0].doc);
        secondResult = searcher.doc(topDocs.scoreDocs[1].doc);
        
        assertEquals("suche suche", firstResult.get("title"));
        assertEquals("suche banane", secondResult.get("title"));
    }

    private Directory indexDocument() throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", "Such Evolution", Field.Store.YES));
        doc.add(new TextField("speaker", "Florian Hopf", Field.Store.YES));
        doc.add(new StringField("date", "20130704", Field.Store.YES));
        return indexDocument(doc);
    }

    private void assertResults(TopDocs topDocs, IndexSearcher searcher) throws IOException {
        assertEquals(1, topDocs.totalHits);
        
        for (ScoreDoc scoreDoc: topDocs.scoreDocs) {
            Document result = searcher.doc(scoreDoc.doc);
            assertEquals("Florian Hopf", result.get("speaker"));
        }
    }

    private Directory indexDocument(Document... doc) throws IOException {
        Directory directory = FSDirectory.open(new File("/tmp/talk-index"));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, new GermanAnalyzer(Version.LUCENE_43));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            for (Document indexDoc: doc) {
                writer.addDocument(indexDoc);
            }
            writer.commit();
        }
        return directory;
    }
}

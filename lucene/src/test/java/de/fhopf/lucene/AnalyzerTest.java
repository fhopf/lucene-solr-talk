package de.fhopf.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: flo
 * Date: 13.06.12
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */
public class AnalyzerTest {

    private String text = "Es gibt viele Freuden in unseres Herrgotts Welt, nur muss man sich auf das Suchen verstehen.";

    @Test
    public void analyzeText() throws IOException {

        Reader reader = new StringReader(text);

        Analyzer analyzer = new GermanAnalyzer(Version.LUCENE_36);

        TokenStream stream = analyzer.tokenStream("dummy", reader);

        CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);

        StringBuilder result = new StringBuilder();

        while (stream.incrementToken()) {
            result.append(termAttribute.toString());
            result.append(" ");
        }

        System.out.println("[" + result.toString() + "]");
        assertEquals("gibt viel freud uns herrgott welt such versteh", result.toString().trim());
    }

    @Test
    public void displayTermDictionary() throws IOException {

        Directory dir = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, new GermanAnalyzer(Version.LUCENE_36));
        IndexWriter writer = new IndexWriter(dir, config);

        Document doc = new Document();
        doc.add(new Field("name", text, Field.Store.NO, Field.Index.ANALYZED));

        writer.addDocument(doc);

        doc = new Document();
        doc.add(new Field("name", "Suchen und Finden", Field.Store.NO, Field.Index.ANALYZED));
        writer.addDocument(doc);

        writer.close();

        IndexReader reader = IndexReader.open(dir);

        TermEnum terms = reader.terms();

        int count = 0;

        while (terms.next()) {
            count++;
            Term term = terms.term();
            System.out.println(term.text());
        }

        assertEquals(9, count);
    }
}

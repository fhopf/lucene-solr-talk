package de.fhopf.lucenesolrtalk.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class TermEnumTest {

    private IndexReader reader;

    @Before
    public void indexDocuments() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, new StandardAnalyzer(Version.LUCENE_36));
        IndexWriter writer = new IndexWriter(directory, config);

        Document doc = new Document();

        doc.add(new Field("Category", "Category1", Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field("Category", "Category2", Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field("Author", "Florian Hopf", Field.Store.NO, Field.Index.NOT_ANALYZED));
        writer.addDocument(doc);

        doc.add(new Field("Category", "Category3", Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field("Category", "Category2", Field.Store.NO, Field.Index.NOT_ANALYZED));
        doc.add(new Field("Author", "Theo Tester", Field.Store.NO, Field.Index.NOT_ANALYZED));
        writer.addDocument(doc);

        writer.close();

        reader = IndexReader.open(directory);
    }

    @Test
    public void flawedEnumeration() throws IOException {
        Utils.logTermDictionary(reader);
        TermEnum terms = reader.terms(new Term("Category"));
        int count = 0;
        // this code is broken, don't use
        while(terms.next()) {
            count++;
            Term term = terms.term();
            System.out.println(term.text());
        }
        assertEquals(2, count);
    }

    @Test
    public void workingEnumeration() throws IOException {
        TermEnum terms = reader.terms(new Term("Category"));
        int count = 0;
        for(Term term = terms.term(); term != null; terms.next(), term = terms.term()) {
            count++;
            System.out.println(term.text());
        }
        assertEquals(3, count);
    }

    @Test
    public void enumerateWithWhile() throws IOException {
        TermEnum terms = reader.terms(new Term("Category"));
        int count = 0;
        if (terms.term() != null) {
            do {
                count++;
                Term term = terms.term();
                System.out.println(term.text());
            } while(terms.next());
        }
        assertEquals(3, count);
    }

    @Test
    public void enumerateWholeIndexActuallyIsntFlawed() throws IOException {
        TermEnum terms = reader.terms();
        int count = 0;
        // if we are reading the whole index it is indeed correct to call
        // next() first
        while(terms.next()) {
            count++;
            Term term = terms.term();
            System.out.println(term.text());
        }
        assertEquals(5, count);
    }

    @After
    public void closeReader() throws IOException {
        reader.close();
    }

}

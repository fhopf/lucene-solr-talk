package de.fhopf.lucene;

import de.fhopf.Talk;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class SearcherTest {

    private Indexer indexer;
    private Searcher searcher;

    @Before
    public void initIndexerAndSearcher() {
        Directory dir = new RAMDirectory();
        indexer = new Indexer(dir);
        searcher = new Searcher(dir);
    }

    @Test
    public void searchForSingleExactCategory() {
        Talk talk1 = newCategoryTalk("Architektur", "Integration");
        Talk talk2 = newCategoryTalk("Architektur");
        Talk talk3 = newCategoryTalk("Integration", "OSGi");
        indexer.index(talk1, talk2, talk3);
        List<Document> documents = searcher.searchCategory("Architektur");
        assertEquals(2, documents.size());
    }

    @Test
    public void searchAuthor() throws ParseException {
        Talk talk1 = newAuthorTalk("Florian Hopf");
        Talk talk2 = newAuthorTalk("Florian");
        indexer.index(talk1, talk2);
        List<Document> documents = searcher.search("speaker:Florian");
        assertEquals(2, documents.size());
        documents = searcher.search("speaker:\"Florian Hopf\"");
        assertEquals(1, documents.size());
    }


    private Talk newAuthorTalk(String author) {
        return new Talk("", "", Arrays.asList(author), new Date(), "", new ArrayList<String>());
    }

    private Talk newCategoryTalk(String... category) {
        return new Talk("", "", new ArrayList<String>(), new Date(), "", Arrays.asList(category));
    }

}

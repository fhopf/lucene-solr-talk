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

import java.util.*;

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

    @Test
    public void allCategories() {
        Talk talk1 = newCategoryTalk("category1", "category3", "category2");
        Talk talk2 = newCategoryTalk("category1", "category4");
        indexer.index(talk1, talk2);

        List<String> categories = searcher.getAllCategories();
        assertEquals(4, categories.size());
        // categories are sorted
        assertEquals("category1", categories.get(0));
        assertEquals("category2", categories.get(1));
        assertEquals("category3", categories.get(2));
        assertEquals("category4", categories.get(3));

    }

    @Test
    public void searchByQueryAndCategory() throws ParseException {
        Talk inCategory = new Talk("", "Titel der trifft", new ArrayList<String>(), new Date(), "", Arrays.asList("cat1", "cat2"));
        Talk inCategoryNoMatch = new Talk("", "kein Treffer", new ArrayList<String>(), new Date(), "", Arrays.asList("cat1", "cat2"));
        Talk notInCategoryWouldMatch = new Talk("", "Titel der trifft", new ArrayList<String>(), new Date(), "", Arrays.asList("cat2"));
        indexer.index(inCategory, inCategoryNoMatch, notInCategoryWouldMatch);

        List<Document> result = searcher.search("Titel", "cat1");
        assertEquals(1, result.size());
        assertEquals("Titel der trifft", result.get(0).get("title"));
    }

    @Test
    public void sortedByDate() throws ParseException {
        Date now = new Date();
        Calendar cal = GregorianCalendar.getInstance();
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = cal.getTime();
        Talk yesterdayMoreRelevant = new Talk("", "Titel Treffer", new ArrayList<String>(), yesterday, "", new ArrayList<String>());
        Talk today = new Talk("", "Titel", new ArrayList<String>(), now, "", new ArrayList<String>());
        indexer.index(yesterdayMoreRelevant, today);

        // sanity check
        List<Document> result = searcher.search("Titel Treffer");
        assertEquals(2, result.size());
        assertEquals("Titel Treffer", result.get(0).get("title"));

        // sort
        result = searcher.searchSortedByDate("Titel", null);
        assertEquals(2, result.size());
        assertEquals("Titel", result.get(0).get("title"));

    }

    private Talk newAuthorTalk(String author) {
        return new Talk("", "", Arrays.asList(author), new Date(), "", new ArrayList<String>());
    }

    private Talk newCategoryTalk(String... category) {
        return new Talk("", "", new ArrayList<String>(), new Date(), "", Arrays.asList(category));
    }

}

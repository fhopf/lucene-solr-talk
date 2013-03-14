package de.fhopf.lucenesolrtalk.web.elasticsearch;

import de.fhopf.elasticsearch.test.ElasticsearchTestNode;
import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.Talk;
import de.fhopf.lucenesolrtalk.elasticsearch.Indexer;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Rule;

/**
 *
 */
public class ElasticsearchSearcherTest {

    @Rule
    public ElasticsearchTestNode testNode = new ElasticsearchTestNode();
    private ElasticsearchSearcher searcher;
    private Indexer indexer;
    private Talk talk = new Talk("/foo/bar", "Testen mit Elasticsearch", Arrays.asList("Florian Hopf"),
            new Date(), "Foo Bar Content", Arrays.asList("Test", "Elasticsearch"));

    @Before
    public void init() throws IOException {
        searcher = new ElasticsearchSearcher(testNode.getClient());
        indexer = new Indexer(testNode.getClient());
        indexer.prepareIndex();
    }

    @Test
    public void zeroResultsOnEmptyIndex() {
        assertTrue(searcher.search("").isEmpty());
    }

    @Test
    public void matchInTitleIsFound() throws IOException {
        indexExampleTalk();
        assertEquals(1, searcher.search("test").size());
    }

    @Test
    public void phraseMatchInTitle() throws IOException {
        indexExampleTalk();
        assertEquals(1, searcher.search("mit Elasticsearch").size());
    }

    @Test
    public void titleIsStored() throws IOException {
        indexExampleTalk();
        Result result = searcher.search("test").get(0);
        assertEquals(talk.title, result.getTitle());
    }

    @Test
    public void emptyQueryReturnsDocuments() throws IOException {
        indexExampleTalk();
        assertEquals(1, searcher.search("").size());
    }
    
    @Test
    public void categoriesAreAvailable() throws IOException {
        Result result = indexAndSearchSingle("test");
        assertEquals(talk.categories.size(), result.getCategories().size());
        for (String category: talk.categories) {
            assertTrue(result.getCategories().contains(category));
        }
    }
    
    @Test
    public void speakersAreAvailable() throws IOException {
        Result result = indexAndSearchSingle("test");
        assertEquals(talk.speakers.size(), result.getSpeakers().size());
        for (String speaker: talk.speakers) {
            assertTrue(speaker + result.getSpeakers(), result.getSpeakers().contains(speaker));
        }
        
    }
    
    @Test
    public void dateIsAvailable() throws IOException {
        Result result = indexAndSearchSingle("test");
        assertEquals(talk.date, result.getDate());
    }
    
    @Test
    public void excerptIsAvailable() throws IOException {
        Result result = indexAndSearchSingle("content");
        assertTrue(result.getExcerpt().contains("Content"));
        // no simple toString
        assertFalse(result.getExcerpt().contains("[content]"));
    }
    
    private Result indexAndSearchSingle(String term) throws IOException {
        indexExampleTalk();
        return searcher.search(term).get(0);
    }

    private void indexExampleTalk() throws IOException {
        indexer.index(Arrays.asList(talk));
        testNode.getClient().admin().indices().prepareRefresh(ElasticsearchSearcher.INDEX).execute().actionGet();
    }
}

package de.fhopf.lucenesolrtalk.web.elasticsearch;

import de.fhopf.elasticsearch.test.ElasticsearchTestNode;
import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.Talk;
import de.fhopf.lucenesolrtalk.elasticsearch.Indexer;
import de.fhopf.lucenesolrtalk.web.Facet;
import de.fhopf.lucenesolrtalk.web.Faceting;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
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
            new Date(), "Foo Bar Content", Arrays.asList("Test", "Elasticsearch"), "");

    @Before
    public void init() throws IOException {
        searcher = new ElasticsearchSearcher(testNode.getClient());
        indexer = new Indexer(testNode.getClient());
        indexer.prepareIndex();
    }

    @Test
    public void zeroResultsOnEmptyIndex() {
        assertTrue(search("").isEmpty());
    }

    @Test
    public void matchInTitleIsFound() throws IOException {
        indexExampleTalk();
        assertEquals(1, search("test").size());
    }

    @Test
    public void phraseMatchInTitle() throws IOException {
        indexExampleTalk();
        assertEquals(1, search("mit Elasticsearch").size());
    }

    @Test
    public void titleIsStored() throws IOException {
        indexExampleTalk();
        Result result = search("test").get(0);
        assertEquals(talk.title, result.getTitle());
    }

    @Test
    public void emptyQueryReturnsDocuments() throws IOException {
        indexExampleTalk();
        assertEquals(1, search("").size());
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
    
    @Test
    public void speakerFacetsAreAvailable() throws IOException {
        Faceting facets = facetsForSampleTalk();
        assertEquals(talk.speakers.size(), facets.getSpeakerFacet().size());
        for (Facet facet: facets.getSpeakerFacet()) {
            assertTrue(facet.getTerm(), talk.speakers.contains(facet.getTerm()));
            assertEquals(1, facet.getCount());
        }
    }
    
    @Test
    public void categoryFacetsAreAvailable() throws IOException {
        Faceting facets = facetsForSampleTalk();
        assertEquals(talk.categories.size(), facets.getCategoryFacet().size());
        for (Facet facet: facets.getCategoryFacet()) {
            assertTrue(facet.getTerm(), talk.categories.contains(facet.getTerm()));
            assertEquals(1, facet.getCount());
        }
    }

    @Test
    public void filtersAreApplied() throws IOException {
        indexExampleTalk();
        
        SearchResponse response = searcher.search("", Arrays.asList("category:SomeCrazyStuff"));
        assertEquals(0, response.hits().getTotalHits());
        
        response = searcher.search("", Arrays.asList("category:" + talk.categories.get(0)));
        assertEquals(1, response.getHits().getTotalHits());
    }
    
    private Result indexAndSearchSingle(String term) throws IOException {
        indexExampleTalk();
        return search(term).get(0);
    }
    
    private Faceting facetsForSampleTalk() throws IOException {
        indexExampleTalk();
        SearchResponse response = searcher.search("*");
        return searcher.getFacets(response);
    }

    private void indexExampleTalk() throws IOException {
        indexer.index(Arrays.asList(talk));
        testNode.getClient().admin().indices().prepareRefresh(ElasticsearchSearcher.INDEX).execute().actionGet();
    }
    
    private List<Result> search(String term) {
        return searcher.getResults(searcher.search(term));
    }
}

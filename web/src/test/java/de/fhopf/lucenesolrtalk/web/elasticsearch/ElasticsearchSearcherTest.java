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

    private void indexExampleTalk() throws IOException {
        indexer.index(Arrays.asList(talk));
        testNode.getClient().admin().indices().prepareRefresh(ElasticsearchSearcher.INDEX).execute().actionGet();
    }
}

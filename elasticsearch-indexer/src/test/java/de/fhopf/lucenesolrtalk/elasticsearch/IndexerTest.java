package de.fhopf.lucenesolrtalk.elasticsearch;

import de.fhopf.elasticsearch.test.ElasticsearchTestNode;
import de.fhopf.lucenesolrtalk.Talk;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;

/**
 *
 */
public class IndexerTest {

    @Rule
    public ElasticsearchTestNode testNode = new ElasticsearchTestNode();
    private Indexer indexer;
    private Talk talk = new Talk("/foo/bar", "Testen mit Elasticsearch", Arrays.asList("Florian Hopf"),
            new Date(), "Foo Bar Content", Arrays.asList("Test", "Elasticsearch"), "");

    @Before
    public void initIndexer() {
        indexer = new Indexer(testNode.getClient());
    }

    @Test
    public void prepareIndexCreatesIndex() throws IOException {
        // no index available when starting
        assertFalse(indexExists(Indexer.INDEX));
        indexer.prepareIndex();

        assertTrue(indexExists(Indexer.INDEX));
    }

    @Test
    public void prepareIndexAndIndex() throws IOException {

        prepareIndexAndIndex(talk);
        assertEquals(1, findAll().hits().totalHits());
    }

    @Test
    public void pathIsId() throws IOException {
        prepareIndexAndIndex(talk);

        SearchHit hit = findAll().hits().getAt(0);
        // path needs to be set as id
        assertEquals(talk.path, hit.getId());
    }
    
    @Test
    public void categoriesAreMultivalued() throws IOException {
        prepareIndexAndIndex(talk);
        SearchHit hit = findAll().hits().getAt(0);
        SearchHitField category = hit.field("category");
        assertEquals(talk.categories.size(), category.getValues().size());
    }
    
    private void prepareIndexAndIndex(Talk talk) throws IOException {
        indexer.prepareIndex();
        indexer.index(Arrays.asList(talk));

        refreshIndex();
    }

    private boolean indexExists(String name) {
        IndicesAdminClient indicesClient = testNode.getClient().admin().indices();
        return indicesClient.prepareExists(name).execute().actionGet().exists();
    }

    /**
     * Triggers a commit so the results are available immediately.
     *
     * @throws ElasticSearchException
     */
    private void refreshIndex() throws ElasticSearchException {
        testNode.getClient().admin().indices().prepareRefresh(Indexer.INDEX).execute().actionGet();
    }

    private SearchResponse findAll() throws ElasticSearchException {
        return testNode.getClient().prepareSearch(Indexer.INDEX).addFields("title", "category").setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
    }
}

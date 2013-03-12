package de.fhopf.lucenesolrtalk.elasticsearch;

import de.fhopf.elasticsearch.test.ElasticsearchTestNode;
import org.junit.Before;
import org.junit.Rule;

/**
 *
 */
public class IndexerTest {

    @Rule
    private ElasticsearchTestNode testNode = new ElasticsearchTestNode();
    
    private Indexer indexer;
    
    @Before
    public void initIndexer() {
        //indexer = new Indexer(testNode.)
    }
    
    public void prepareIndexCreatesIndex() {
        
    }
    
}

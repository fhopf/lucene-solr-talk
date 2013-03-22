package de.fhopf.elasticsearch.test;

import java.io.IOException;
import org.elasticsearch.action.get.GetResponse;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*; 
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.junit.Rule;

/**
 * Execute some core indexing and searching.
 */
public class CoreTest {

    @Rule
    public ElasticsearchTestNode client = new ElasticsearchTestNode();
    
    @Test
    public void indexAndGet() throws IOException {
        client.getClient().prepareIndex("myindex", "document", "1")
                .setSource(jsonBuilder().startObject().field("test", "123").endObject())
                .execute()
                .actionGet();
        
        GetResponse response = client.getClient().prepareGet("myindex", "document", "1").execute().actionGet();
        assertThat(response.getSource().get("test")).isEqualTo("123");
    }
    
    
}

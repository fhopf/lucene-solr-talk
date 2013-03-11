package de.fhopf.elasticsearch;

import org.elasticsearch.client.Client;
import org.junit.rules.ExternalResource;

/**
 * A JUnit rule that creates an elasticsearch client that writes to a temp dir.
 * @author Florian Hopf
 */
public class ElasticsearchTestNode extends ExternalResource {

    private EmbeddedElasticsearchServer server;
    private String elastichome;
    
    public ElasticsearchTestNode() {
        this(null);
    }
    
    public ElasticsearchTestNode(String elastichome) {
        this.elastichome = elastichome;
    }
    
    @Override
    protected void before() throws Throwable {
        server = new EmbeddedElasticsearchServer(elastichome);
    }

    @Override
    protected void after() {
        server.shutdown();
    }
    
    public Client getClient() {
        return server.getClient();
    }       
    
}

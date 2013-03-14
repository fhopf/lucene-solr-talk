package de.fhopf.lucenesolrtalk.web.solr;

import com.yammer.metrics.core.HealthCheck;
import java.io.IOException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

/**
 */
public class SolrHealthCheck extends HealthCheck {
    private final SolrServer server;

    public SolrHealthCheck(SolrServer server) {
        super("Solr");
        this.server = server;
    }
    
    @Override
    protected Result check() throws Exception {
        try {
            server.ping();
            return Result.healthy();
        } catch (SolrServerException | IOException ex) {
            return Result.unhealthy(ex);
        }
        
    }
    
}

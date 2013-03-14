package de.fhopf.lucenesolrtalk.web.elasticsearch;

import com.yammer.metrics.core.HealthCheck;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;

/**
 * 
 */
public class ElasticsearchHealthCheck extends HealthCheck {
    private final Client client;

    public ElasticsearchHealthCheck(Client client) {
        super("Elasticsearch");
        this.client = client;
    }
    
    @Override
    protected Result check() throws Exception {
        ClusterHealthResponse health = client.admin().cluster().prepareHealth(ElasticsearchSearcher.INDEX).execute().actionGet();
        // though it sounds bad, status yellow is in fact ok for our setup
        if (health.getStatus() != ClusterHealthStatus.RED) {
            return Result.healthy();
        } else {
            return Result.unhealthy(health.getStatus().name());
        }
        
    }
    
}

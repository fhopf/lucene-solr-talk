package de.fhopf.lucenesolrtalk.web;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;
import de.fhopf.lucenesolrtalk.web.elasticsearch.ElasticsearchResource;
import de.fhopf.lucenesolrtalk.web.elasticsearch.ElasticsearchSearcher;
import de.fhopf.lucenesolrtalk.web.lucene.LuceneSearchResource;
import de.fhopf.lucenesolrtalk.web.solr.SolrSearchResource;
import de.fhopf.lucenesolrtalk.web.solr.SolrSearcher;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.NodeBuilder;

public class SearchService extends Service<SearchConfiguration> {

    public SearchService() {
        super("search");
        addBundle(new ViewBundle());
        addBundle(new AssetsBundle());
    }

    @Override
    protected void initialize(SearchConfiguration searchConfiguration, Environment environment) throws Exception {
        environment.addResource(new LuceneSearchResource(searchConfiguration.indexDir));
        SolrServer server = new HttpSolrServer(searchConfiguration.solrUrl);
        environment.addResource(new SolrSearchResource(new SolrSearcher(server)));
        Client esClient = NodeBuilder.nodeBuilder().client(true).node().client();
        environment.addResource(new ElasticsearchResource(new ElasticsearchSearcher(esClient)));        
    }

    public static void main(String [] args) throws Exception {
        new SearchService().run(args);
    }


}

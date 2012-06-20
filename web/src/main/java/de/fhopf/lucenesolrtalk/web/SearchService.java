package de.fhopf.lucenesolrtalk.web;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;
import de.fhopf.lucenesolrtalk.web.lucene.LuceneSearchResource;
import de.fhopf.lucenesolrtalk.web.solr.SolrSearchResource;
import de.fhopf.lucenesolrtalk.web.solr.SolrSearcher;

public class SearchService extends Service<SearchConfiguration> {

    public SearchService() {
        super("search");
        addBundle(new ViewBundle());
        addBundle(new AssetsBundle());
    }

    @Override
    protected void initialize(SearchConfiguration luceneSearchConfiguration, Environment environment) throws Exception {
        environment.addResource(new LuceneSearchResource(luceneSearchConfiguration.getIndexDir()));
        environment.addResource(new SolrSearchResource(new SolrSearcher()));
    }

    public static void main(String [] args) throws Exception {
        new SearchService().run(args);
    }


}

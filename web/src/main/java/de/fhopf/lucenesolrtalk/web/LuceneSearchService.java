package de.fhopf.lucenesolrtalk.web;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

public class LuceneSearchService extends Service<LuceneSearchConfiguration> {

    public LuceneSearchService() {
        super("lucene-search");
        addBundle(new ViewBundle());
    }

    @Override
    protected void initialize(LuceneSearchConfiguration luceneSearchConfiguration, Environment environment) throws Exception {
        environment.addResource(new LuceneSearchResource(luceneSearchConfiguration.getIndexDir()));
    }

    public static void main(String [] args) throws Exception {
        new LuceneSearchService().run(args);
    }


}

package de.fhopf.lucenesolrtalk.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.util.AbstractSolrTestCase;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class BasicConfigurationTest extends AbstractSolrTestCase {

    private EmbeddedSolrServer server;

//    @BeforeClass
//    public static void initCore() throws Exception {
//        SolrTestCaseJ4.initCore("solr/conf/solrconfig.xml", "solr/conf/schema.xml");
//    }
@Override
public String getSchemaFile() {
    return "solr/conf/schema.xml";
}

    @Override
    public String getSolrConfigFile() {
        return "solr/conf/solrconfig.xml";
    }

    @Before
    public void initSolrServer() throws Exception {
        super.setUp();
        server = new EmbeddedSolrServer(h.getCoreContainer(), "");
    }

    @Test
    public void queryIsNotFound() throws SolrServerException {
//                assertQ("test query on empty index",
//            req("qlkciyopsbgzyvkylsjhchghjrdf")
//            ,"//result[@numFound='0']"
//            );



        SolrParams params = new SolrQuery("text that is not found");
        QueryResponse response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());

        server.getCoreContainer().shutdown();
    }

    //@Test(expected = SolrServerException.class)
    public void pathIsMandatory() throws SolrServerException, IOException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("title", "the title");

        server.add(document);
        server.commit();
    }

    //@Test
    public void simpleDocumentIsIndexedAndFound() throws SolrServerException, IOException {
       server.commit();
    }

    @After
    public void releaseSolr() throws Exception {
        super.tearDown();
        server.shutdown();

    }

}

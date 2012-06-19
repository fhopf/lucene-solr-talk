package de.fhopf.lucenesolrtalk.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class ServerBasedTalkTest extends SolrTestCaseJ4 {

    private EmbeddedSolrServer server;

    @BeforeClass
    public static void initCore() throws Exception {
        SolrTestCaseJ4.initCore("solr/conf/solrconfig.xml", "solr/conf/schema.xml");
    }

    @Before
    public void initServer() {
        server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());
    }

    @Test
    public void queryOnEmptyIndexNoResults() throws SolrServerException {
        QueryResponse response = server.query(new SolrQuery("text that is not found"));
        assertTrue(response.getResults().isEmpty());
    }

    @Test
    public void singleDocumentIsFound() throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("path", "/tmp/foo");
        document.addField("content", "Mein Hut der hat 4 Ecken");

        server.add(document);
        server.commit();

        SolrParams params = new SolrQuery("ecke");
        QueryResponse response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        assertEquals("/tmp/foo", response.getResults().get(0).get("path"));
    }

    @Test
    public void facettingOnCatgory() throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("path", "/tmp/foo");
        document.addField("content", "Mein Hut der hat 4 Ecken");
        document.addField("category", "cat1");
        document.addField("category", "cat2");
        server.add(document);

        document = new SolrInputDocument();
        document.addField("path", "/tmp/bar");
        document.addField("category", "cat1");
        document.addField("category", "cat3");
        document.addField("content", "Mein Hut der hat 4 Ecken");
        server.add(document);

        server.commit();

        QueryResponse response = server.query(new SolrQuery("ecke"));
        assertEquals(2L, response.getResults().getNumFound());
        FacetField facet = response.getFacetField("category");
        assertNotNull(facet);
        assertEquals(3, facet.getValueCount());
        FacetField.Count cat1 = facet.getValues().get(0);
        assertEquals("cat1", cat1.getName());
        assertEquals(2, cat1.getCount());

    }

    @After
    public void clearIndex() {
        super.clearIndex();
    }
}

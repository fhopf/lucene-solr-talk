package de.fhopf.lucenesolrtalk.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * TODO: this test always uses the first request handler defined in the config
 * why is that?
 */
public class ServerBasedTalkTest extends SolrTestCaseJ4 {

    private EmbeddedSolrServer server;
    private SolrParams query;

    @BeforeClass
    public static void initCore() throws Exception {
        SolrTestCaseJ4.initCore("solrhome/conf/solrconfig.xml", "solrhome/conf/schema.xml", "solrhome/");
    }

    @Before
    public void initServer() {
        server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());
    }

    @Test
    public void queryOnEmptyIndexNoResults() throws SolrServerException {
        QueryResponse response = server.query(query("text that is not found"));
        assertTrue(response.getResults().isEmpty());
    }

    private SolrQuery query(String text) {
        SolrQuery query = new SolrQuery(text);
        return query.setRequestHandler("/jugka");
    }

    @Test
    public void singleDocumentIsFound() throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("path", "/tmp/foo");
        document.addField("content", "Mein Hut der hat 4 Ecken");

        server.add(document);
        server.commit();

        QueryResponse response = server.query(query("ecke"));
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

        QueryResponse response = server.query(query("ecke"));
        assertEquals(2L, response.getResults().getNumFound());
        FacetField facet = response.getFacetField("category");
        assertNotNull(facet);
        assertEquals(3, facet.getValueCount());
        FacetField.Count cat1 = facet.getValues().get(0);
        assertEquals("cat1", cat1.getName());
        assertEquals(2, cat1.getCount());
    }

    @Test
    public void matchIsHighlighted() throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("path", "/tmp/foo");
        document.addField("content", "Mein Hut der hat 4 Ecken");
        document.addField("category", "cat1");
        document.addField("category", "cat2");
        server.add(document);
        server.commit();

        QueryResponse response = server.query(query("hut"));
        assertEquals(1, response.getResults().size());
        assertNotNull(response.getHighlighting());
        Map<String, List<String>> fragments = response.getHighlighting().get("/tmp/foo");
        assertEquals(1, fragments.size());
        assertTrue(fragments.get("content").get(0).matches(".*<b style=.*>Hut</b>.*"));
    }

    @Test
    public void indexAndSearch() throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("path", "/tmp/foo");
        document.addField("title", "Apache Karaf");
        document.addField("category", "OSGi");
        server.add(document);
        server.commit();

        SolrQuery solrQuery = new SolrQuery("apache");
        solrQuery.setRequestHandler("/jugka");
        QueryResponse response = server.query(solrQuery);
        assertEquals(1, response.getResults().size());
        assertEquals("Apache Karaf", response.getResults().get(0).get("title"));
    }

    @After
    public void clearIndex() {
        super.clearIndex();
    }
}

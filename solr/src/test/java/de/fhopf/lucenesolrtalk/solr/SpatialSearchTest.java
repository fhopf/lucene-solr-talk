package de.fhopf.lucenesolrtalk.solr;

import java.io.IOException;
import java.util.Map;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.request.SolrQueryRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import org.junit.Ignore;

public class SpatialSearchTest extends SolrTestCaseJ4 {

    @BeforeClass
    public static void initSolr() throws Exception {
        SolrTestCaseJ4.initCore("solrconfig.xml", "schema.xml", "solrhome/");
    }

    @Before
    public void indexDocument() {
        assertU(adoc("path", "/tmp/foo", "content", "karlsruhe", "location", "49.487036,8.458001"));
        assertU(adoc("path", "/tmp/bar", "content", "mannheim", "location", "49.013787,8.419936"));
        assertU(commit());
    }

    @After
    public void deleteDocument() {
        clearIndex();
    }
    
    @Test
    public void locationCanBeIndexedAndIsStored() {
        SolrQueryRequest req = req("q", "karlsruhe", "qt", "/jugka");
        assertQ("location is stored", req, 
                "//result[@numFound='1']", 
                "//str[@name='location']/text()='49.487036,8.458001'");
    }
    
    @Ignore("Only valid for LatLonType")
    @Test
    public void twoSubfieldsAreAvailable() throws SolrServerException, IOException {
        LukeRequest request = new LukeRequest();
        LukeResponse response = request.process(new EmbeddedSolrServer(h.getCoreContainer(), "collection1"));
        for (String name: response.getFieldInfo().keySet()) {
            System.out.println(name);
        }
        
        assertThat(response.getFieldInfo().keySet(), hasItem("location_0_coordinate"));
        assertThat(response.getFieldInfo().keySet(), hasItem("location_1_coordinate"));
    }
    
    @Ignore("Only valid for LatLonType")
    @Test
    public void latitudeIsA8PrecisionTrieField() throws Exception {
        SolrQueryRequest req = req("q", "test", "qt", "/terms", "terms.fl", "location_0_coordinate");
        System.out.println(h.query(req));
        // precision might not be exact so we are testing on the whole number
        assertQ("latititude is indexed", req, 
                "count(//lst[@name='location_0_coordinate']/int)=8");
    }

    @Ignore("Only valid for LatLonType")
    @Test
    public void docCanBeFoundByLatitude() throws Exception {
        SolrQueryRequest req = req("q", "49.487036", "qt", "/jug", "qf", "location_0_coordinate");
        assertQ("find by latitude", req, 
                "//result[@numFound='1']");
    }
    
    @Ignore("Only valid for LatLonType")
    @Test
    public void docCanBeFoundByLongitude() {
        SolrQueryRequest req = req("q", "8.458001", "qt", "/jug", "qf", "location_1_coordinate");
        assertQ("find by longitude", req, 
                "//result[@numFound='1']");
    }
    
    @Test
    public void sortByLocationFromMannheim() throws Exception {
        SolrQueryRequest req = req("q", "{!geofilt score=distance sfield=location pt=49.013787,8.419936 d=100}", "qt", "/jug");
        assertQ("sort by location from mannheim", req, 
                "//result[@numFound='2']");
    }
    
    @Test
    public void distanceInfluencesResults() {
        SolrQueryRequest req = req("q", "{!geofilt score=distance sfield=location pt=49.013787,8.419936 d=10}", "qt", "/jug");
        assertQ("geofilt distance removes results", req, 
                "//result[@numFound='1']");
    }
}

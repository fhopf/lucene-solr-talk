package de.fhopf.lucenesolrtalk.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.request.SolrQueryRequest;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;


public class BasicConfigurationTest extends SolrTestCaseJ4 {

    @BeforeClass
    public static void initSolr() throws Exception {
        SolrTestCaseJ4.initCore("solrconfig.xml", "schema.xml", "solrhome/");
    }

    @Test
    public void noResultInEmptyIndex() throws SolrServerException {
        assertQ("test query on empty index",
                jugkaRequest("text that is not found")
                , "//result[@numFound='0']"
        );
    }

    private static SolrQueryRequest jugkaRequest(String s) {
        return req("q", s, "qt", "/jugka");
    }

    @Test
    public void pathIsMandatory() throws SolrServerException, IOException {
        assertFailedU(adoc("title", "the title"));
    }

    @Test
    public void simpleDocumentIsIndexedAndFound() throws SolrServerException, IOException {
        assertU(adoc("path", "/tmp/foo", "content", "Some important content."));
        assertU(commit());

        assertQ("added document found",
                jugkaRequest("important")
                , "//result[@numFound='1']"
        );
    }

    @Test
    @Ignore("q.alt only works for dismax which makes more tests fail")
    public void allDocsAreReturnedWhenQueryMisses() {
        assertU(adoc("path", "/tmp/foo", "content", "Some important content."));
        assertU(adoc("path", "/tmp/bar", "content", "more content."));

        assertU(commit());

        assertQ("added document found",
                lrf.makeRequest("qt", "/jugka")
                , "//result[@numFound='2']"
        );
    }

}

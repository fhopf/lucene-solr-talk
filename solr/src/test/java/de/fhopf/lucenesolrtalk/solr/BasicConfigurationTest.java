package de.fhopf.lucenesolrtalk.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;


public class BasicConfigurationTest extends SolrTestCaseJ4 {

    @BeforeClass
    public static void initCore() throws Exception {
        SolrTestCaseJ4.initCore("solrhome/conf/solrconfig.xml", "solrhome/conf/schema.xml", "solrhome/");
    }

    @Test
    public void noResultInEmptyIndex() throws SolrServerException {
        assertQ("test query on empty index",
                req("text that is not found")
                , "//result[@numFound='0']"
        );
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
                req("important")
                , "//result[@numFound='1']"
        );
    }

}

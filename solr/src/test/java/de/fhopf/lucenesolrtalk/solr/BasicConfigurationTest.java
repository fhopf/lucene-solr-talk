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


public class BasicConfigurationTest extends SolrTestCaseJ4 {

    @BeforeClass
    public static void initCore() throws Exception {
        SolrTestCaseJ4.initCore("solr/conf/solrconfig.xml", "solr/conf/schema.xml");
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
        assertU(adoc("path", "/tmp/foo", "title", "the title"));
        assertU(commit());

        assertQ("added document found",
                req("title:title")
                , "//result[@numFound='1']"
        );
    }

}

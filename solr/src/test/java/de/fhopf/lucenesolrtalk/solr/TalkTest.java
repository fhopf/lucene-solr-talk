package de.fhopf.lucenesolrtalk.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests some queries for talks.
 */
public class TalkTest extends SolrTestCaseJ4 {

    @BeforeClass
    public static void initCore() throws Exception {
        SolrTestCaseJ4.initCore("solr/conf/solrconfig.xml", "solr/conf/schema.xml");
    }

    @Test
    public void valueInContentIsFound() {
        assertU(adoc("path", "/tmp/foo", "title", "Der Titel", "content", "Der Inhalt", "speaker", "Theo Tester", "category", "foo"));
        assertU(commit());

        assertQ(req("Inhalt"), "//result[@numFound='1']",
                "//doc/str[@name='title'][text()='Der Titel']",
                "//doc/arr[@name='speaker']/str[text()='Theo Tester']",
                "//doc/arr[@name='category']/str[text()='foo']",
                "//doc/str[@name='path'][text()='/tmp/foo']");
    }

    @Test
    public void singularIsFound() {
        assertU(adoc("path", "/tmp/foo", "title", "Der Titel", "content", "Die Inhalte", "speaker", "Theo Tester", "category", "foo"));
        assertU(commit());

        assertQ(req("Inhalt"), "//result[@numFound='1']");
    }

    @Test
    public void defaultIsAndQuery() {
        assertU(adoc("path", "/tmp/foo", "title", "Der Titel", "content", "Die Inhalte Dokument 1", "speaker", "Theo Tester", "category", "foo"));
        assertU(adoc("path", "/tmp/foo2", "title", "Der Titel", "content", "Noch ein Dokument", "speaker", "Theo Tester", "category", "foo"));
        assertU(commit());

        assertQ(req("Inhalt Dokument"), "//result[@numFound='1']");
    }
}

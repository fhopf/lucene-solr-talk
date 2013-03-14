package de.fhopf.lucenesolrtalk.web;

import org.apache.solr.client.solrj.util.ClientUtils;

/**
 * Represents a single facet result.
 */
public class Facet {
    
    private final String term;
    private final long count;

    private final String fq;

    public Facet(String term, long count, String fieldname) {
        this.term = term;
        this.count = count;
        this.fq = fieldname.concat(":").concat(ClientUtils.escapeQueryChars(term));
    }

    public String getTerm() {
        return term;
    }

    public long getCount() {
        return count;
    }

    public String getFq() {
        return fq;
    }
}

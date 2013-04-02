package de.fhopf.lucenesolrtalk.web;

import org.apache.solr.client.solrj.util.ClientUtils;

/**
 * Represents a single facet result.
 */
public class Facet {
    
    private final String term;
    private final long count;

    private final String fq;

    private Facet(String displayTerm, long count, String fq) {
        this.term = displayTerm;
        this.count = count;
        this.fq = fq;
    }

    public static Facet termFacet(String term, long count, String fieldname) {
        return new Facet(term, count, fieldname.concat(":").concat(ClientUtils.escapeQueryChars(term)));
    }
    
    public static Facet withFilterQuery(String term, long count, String fq) {
        return new Facet(term, count, fq);
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

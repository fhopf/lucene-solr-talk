package de.fhopf.lucenesolrtalk.web.solr;

import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.web.Faceting;

import java.util.List;

public class SolrSearchResult {

    public final List<Result> results;
    public final Faceting faceting;

    public SolrSearchResult(List<Result> results, Faceting faceting) {
        this.results = results;
        this.faceting = faceting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SolrSearchResult that = (SolrSearchResult) o;

        if (results != null ? !results.equals(that.results) : that.results != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return results != null ? results.hashCode() : 0;
    }
}

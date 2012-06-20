package de.fhopf.lucenesolrtalk.web.solr;

import de.fhopf.Result;
import org.apache.solr.client.solrj.response.FacetField;

import java.util.List;

public class SolrSearchResult {

    public final List<Result> results;
    public final FacetField categoryFacet;
    public final FacetField speakerFacet;
    public final FacetField typeFacet;

    public SolrSearchResult(List<Result> results, FacetField categoryFacet, FacetField speakerFacet, FacetField typeFacet) {
        this.results = results;
        this.categoryFacet = categoryFacet;
        this.speakerFacet = speakerFacet;
        this.typeFacet = typeFacet;
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

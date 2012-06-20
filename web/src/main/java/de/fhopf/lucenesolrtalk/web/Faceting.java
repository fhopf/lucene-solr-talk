package de.fhopf.lucenesolrtalk.web;

import org.apache.solr.client.solrj.response.FacetField;

public class Faceting {

    private final FacetField categoryFacet;
    private final FacetField speakerFacet;
    private final FacetField typeFacet;

    public Faceting(FacetField categoryFacet, FacetField speakerFacet, FacetField typeFacet) {
        this.categoryFacet = categoryFacet;
        this.speakerFacet = speakerFacet;
        this.typeFacet = typeFacet;
    }

    public FacetField getCategoryFacet() {
        return categoryFacet;
    }

    public FacetField getSpeakerFacet() {
        return speakerFacet;
    }

    public FacetField getTypeFacet() {
        return typeFacet;
    }
}

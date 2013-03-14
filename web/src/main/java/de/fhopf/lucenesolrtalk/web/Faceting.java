package de.fhopf.lucenesolrtalk.web;

import java.util.List;

public class Faceting {

    private final List<Facet> categoryFacet;
    private final List<Facet> speakerFacet;

    public Faceting(List<Facet> categoryFacet, List<Facet> speakerFacet) {
        this.categoryFacet = categoryFacet;
        this.speakerFacet = speakerFacet;
    }

    public List<Facet> getCategoryFacet() {
        return categoryFacet;
    }

    public List<Facet> getSpeakerFacet() {
        return speakerFacet;
    }

}

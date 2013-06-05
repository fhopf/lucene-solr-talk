package de.fhopf.lucenesolrtalk.web;

import java.util.List;

public class Faceting {

    private final List<Facet> categoryFacet;
    private final List<Facet> speakerFacet;
    private final List<Facet> dateFacet;
    private final List<Facet> organizerFacet;

    public Faceting(List<Facet> categoryFacet, List<Facet> speakerFacet, List<Facet> dateFacet, List<Facet> organizerFacet) {
        this.categoryFacet = categoryFacet;
        this.speakerFacet = speakerFacet;
        this.dateFacet = dateFacet;
        this.organizerFacet = organizerFacet;
    }

    public List<Facet> getCategoryFacet() {
        return categoryFacet;
    }

    public List<Facet> getSpeakerFacet() {
        return speakerFacet;
    }
    
    public List<Facet> getDateFacet() {
        return dateFacet;
    }

    public List<Facet> getOrganizerFacet() {
        return organizerFacet;
    }
}

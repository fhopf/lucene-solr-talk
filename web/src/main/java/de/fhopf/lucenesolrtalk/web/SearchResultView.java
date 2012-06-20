package de.fhopf.lucenesolrtalk.web;

import com.yammer.dropwizard.views.View;
import de.fhopf.Result;

import java.util.List;

public class SearchResultView extends View {

    private final Categories categories;
    private final String query;
    private final List<Result> results;
    private final Faceting faceting;
    private String path;

    public SearchResultView(String query, List<Result> results, Categories categories) {
        this(query, results, categories, null);
        // should be moved to the resource
        this.path = "/lucene/";
    }

    public SearchResultView(String query, List<Result> results, Categories categories, Faceting faceting) {
        super("result.fmt");
        this.query = query;
        this.results = results;
        this.categories = categories;
        this.faceting = faceting;
        // should be moved to the resource
        this.path = "/solr/";
    }

    public Categories getCategories() {
        return categories;
    }

    public String getQuery() {
        return query;
    }

    public List<Result> getResults() {
        return results;
    }

    public Faceting getFaceting() {
        return faceting;
    }

    public String getPath() {
        return path;
    }
}

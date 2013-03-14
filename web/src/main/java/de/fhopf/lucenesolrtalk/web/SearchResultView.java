package de.fhopf.lucenesolrtalk.web;

import com.yammer.dropwizard.views.View;
import de.fhopf.lucenesolrtalk.Result;

import java.util.List;

public class SearchResultView extends View {

    private final Categories categories;
    private final String query;
    private final List<Result> results;
    private final Faceting faceting;
    private final String path;
    private String currentQuery;

    public SearchResultView(String path, String query, List<Result> results, Categories categories) {
        this(path, query, results, categories, null, null);
    }

    public SearchResultView(String path, String query, List<Result> results, Categories categories, Faceting faceting, String currentQuery) {
        super("result.fmt");
        this.query = query;
        this.results = results;
        this.categories = categories;
        this.faceting = faceting;
        this.currentQuery = currentQuery;
        this.path = path;
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

    public String getCurrentQuery() {
        return currentQuery;
    }
}

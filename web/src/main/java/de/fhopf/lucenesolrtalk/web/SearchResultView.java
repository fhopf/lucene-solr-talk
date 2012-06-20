package de.fhopf.lucenesolrtalk.web;

import com.yammer.dropwizard.views.View;
import de.fhopf.Result;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flo
 * Date: 13.06.12
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultView extends View {

    private final List<String> categories;
    private final String query;
    private final List<Result> results;

    public SearchResultView(String query, List<Result> results, List<String> categories) {
        super("result.fmt");
        this.query = query;
        this.results = results;
        this.categories = categories;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getQuery() {
        return query;
    }

    public List<Result> getResults() {
        return results;
    }
}

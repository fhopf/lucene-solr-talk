package de.fhopf.lucenesolrtalk.web;

import java.util.List;

public class Result {

    private final String title;
    private final String excerpt;
    private final List<String> categories;

    public Result(String title, String excerpt, List<String> categories) {
        this.title = title;
        this.excerpt = excerpt;
        this.categories = categories;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCategories() {
        return categories;
    }
}

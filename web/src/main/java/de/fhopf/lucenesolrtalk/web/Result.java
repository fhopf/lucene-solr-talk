package de.fhopf.lucenesolrtalk.web;

import java.util.List;

public class Result {

    private final String title;
    private final String excerpt;

    public Result(String title, String excerpt) {
        this.title = title;
        this.excerpt = excerpt;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getTitle() {
        return title;
    }
}

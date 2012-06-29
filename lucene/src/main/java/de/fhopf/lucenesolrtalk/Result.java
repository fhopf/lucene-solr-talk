package de.fhopf.lucenesolrtalk;

import java.util.Date;
import java.util.List;

/**
 * Represents a search result from the index.
 */
public class Result {

    private final String title;
    private final String excerpt;
    private final List<String> categories;
    private final List<String> speakers;
    private final Date date;

    public Result(String title, String excerpt, List<String> categories, List<String> speakers, Date date) {
        this.title = title;
        this.excerpt = excerpt;
        this.categories = categories;
        this.speakers = speakers;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getSpeakers() {
        return speakers;
    }

    public Date getDate() {
        return date;
    }
}

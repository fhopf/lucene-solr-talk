package de.fhopf.lucenesolrtalk;

import java.util.Date;
import java.util.List;

/**
 * Represents a talk to be indexed.
 */
public class Talk {

    public final String path;
    public final String title;
    public final List<String> speakers;
    public final Date date;
    public final String content;
    public final List<String> categories;

    public Talk(String path, String title, List<String> speakers, Date date, String content, List<String> categories) {
        this.path = path;
        this.title = title;
        this.speakers = speakers;
        this.date = date;
        this.content = content;
        this.categories = categories;
    }
}

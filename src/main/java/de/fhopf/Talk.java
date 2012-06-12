package de.fhopf;

import org.apache.lucene.search.FieldComparator;

import java.util.Date;

/**
 * Represents a talk to be indexed.
 */
public class Talk {

    public final String path;
    public final String title;
    public final String author;
    public final Date date;
    public final String content;

    public Talk(String path, String title, String author, Date date, String content) {
        this.path = path;
        this.title = title;
        this.author = author;
        this.date = date;
        this.content = content;
    }
}

package de.fhopf;

import java.util.Date;

/**
 * Represents a talk to be indexed.
 */
public class Talk {

    public final String path;
    public final String title;
    public final String speaker;
    public final Date date;
    public final String content;

    public Talk(String path, String title, String speaker, Date date, String content) {
        this.path = path;
        this.title = title;
        this.speaker = speaker;
        this.date = date;
        this.content = content;
    }
}

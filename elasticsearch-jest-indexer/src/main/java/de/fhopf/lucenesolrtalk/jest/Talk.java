package de.fhopf.lucenesolrtalk.jest;

import java.util.Date;
import java.util.List;

/**
 * A talk that is a bean as expected by Jest.
 * @author Florian Hopf
 */
public class Talk {
    
    private String path;
    private String title;
    private List<String> speakers;
    private Date date;
    private String content;
    private List<String> categories;
    private String organizer;

    public Talk() {
        
    }
    
    public Talk(String path, String title, List<String> speakers, Date date, String content, List<String> categories, String organizer) {
        this.path = path;
        this.title = title;
        this.speakers = speakers;
        this.date = date;
        this.content = content;
        this.categories = categories;
        this.organizer = organizer;
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<String> speakers) {
        this.speakers = speakers;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
}

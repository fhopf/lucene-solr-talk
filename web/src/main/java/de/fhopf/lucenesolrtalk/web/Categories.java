package de.fhopf.lucenesolrtalk.web;

import java.util.Iterator;
import java.util.List;

public class Categories implements Iterable<String> {

    private final List<String> categories;
    private final String currentCategory;

    public Categories(List<String> categories, String currentCategory) {
        this.categories = categories;
        this.currentCategory = currentCategory;
    }

    @Override
    public Iterator<String> iterator() {
        return categories.iterator();
    }

    public String getCurrent() {
        return currentCategory;
    }
}

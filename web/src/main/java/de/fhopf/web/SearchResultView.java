package de.fhopf.web;

import com.yammer.dropwizard.views.View;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flo
 * Date: 13.06.12
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultView extends View {

    private List<String> categories;


    protected SearchResultView() {
        super("");
    }
}

package de.fhopf.lucenesolrtalk.web;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collections;

@Path("/lucene")
@Produces(MediaType.TEXT_HTML)
public class LuceneSearchResource {

    private final String indexDir;

    public LuceneSearchResource(String indexDir) {
        this.indexDir = indexDir;
    }

    @GET
    @Timed
    public SearchResultView search(@QueryParam("query")Optional<String> query) {
        Result dummy1 = new Result("Titel 1", "Excerpt 1", Arrays.asList("Kat1", "Kat2"));
        Result dummy2 = new Result("Titel 2", "Excerpt 2", Arrays.asList("Kat2", "Kat3"));
        return new SearchResultView(query.or("-"), Arrays.asList(dummy1, dummy2), Collections.<String>emptyList());
    }
}

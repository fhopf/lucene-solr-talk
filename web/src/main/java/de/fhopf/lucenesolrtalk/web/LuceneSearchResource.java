package de.fhopf.lucenesolrtalk.web;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;

@Path("/lucene")
@Produces(MediaType.APPLICATION_JSON)
public class LuceneSearchResource {

    private final String indexDir;

    public LuceneSearchResource(String indexDir) {
        this.indexDir = indexDir;
    }

    @GET
    @Timed
    public Result search(@QueryParam("query")Optional<String> query) {
        return new Result(Arrays.asList(query.or("a"), "b", "c"));
    }
}

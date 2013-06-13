package de.fhopf.lucenesolrtalk.web.lucene;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.lucene.Searcher;
import de.fhopf.lucenesolrtalk.web.SearchResultView;
import org.apache.lucene.store.FSDirectory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;

@Path("/lucene")
@Produces("text/html; charset=utf-8")
public class LuceneSearchResource {

    private final Searcher searcher;

    public LuceneSearchResource(String indexDir) {
        try {
            this.searcher = new Searcher(FSDirectory.open(new File(indexDir)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @GET
    @Timed
    public SearchResultView search(@QueryParam("query")Optional<String> query, @QueryParam("sort") Optional<String> sort) throws ParseException {
        List<Result> results = Collections.emptyList();
        if (query.isPresent()) {
            if ("date".equals(sort.or(""))) {
                results = searcher.searchSortedByDate(query.get());
            } else {
                results = searcher.search(query.get());
            }
        }

        return new SearchResultView("/lucene/", query.or("-"), results, null);
    }
}

package de.fhopf.lucenesolrtalk.web.solr;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import de.fhopf.Result;
import de.fhopf.lucenesolrtalk.web.SearchResultView;
import org.apache.lucene.queryParser.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@Path("/solr")
@Produces(MediaType.TEXT_HTML)
public class SolrSearchResource {

    private final SolrSearcher searcher;

    public SolrSearchResource(SolrSearcher searcher) {
        this.searcher = searcher;
    }

    @GET
    @Timed
    public SearchResultView search(@QueryParam("query")Optional<String> query, @QueryParam("sort") Optional<String> sort,
                                   @QueryParam("category") Optional<String> category) throws ParseException {
        List<Result> results = Collections.emptyList();
        List<String> categories = searcher.getAllCategories();
//        if (query.isPresent()) {
//            if ("date".equals(sort.or(""))) {
//                results = searcher.searchSortedByDate(query.get(), category);
//            } else {
//                results = searcher.search(query.get(), category);
//            }
//        }

        return new SearchResultView(query.or("-"), results, categories);
    }

}

package de.fhopf.lucenesolrtalk.web.elasticsearch;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.web.Faceting;
import de.fhopf.lucenesolrtalk.web.SearchResultView;
import de.fhopf.lucenesolrtalk.web.solr.SolrSearchResult;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 */
@Path("/elasticsearch")
@Produces("text/html; charset=utf-8")
public class ElasticsearchResource {

    private final ElasticsearchSearcher searcher;

    public ElasticsearchResource(ElasticsearchSearcher searcher) {
        this.searcher = searcher;
    }

    @GET
    @Timed
    public SearchResultView search(@QueryParam("query") Optional<String> query, @QueryParam("sort") Optional<String> sort,
            @QueryParam("fq") Set<String> fqs) throws SolrServerException {
        
        List<Result> results = searcher.search(query.or(""));
        
        return new SearchResultView(query.or(""), results);
    }
}

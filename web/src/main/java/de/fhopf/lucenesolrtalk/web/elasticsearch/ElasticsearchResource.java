package de.fhopf.lucenesolrtalk.web.elasticsearch;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.web.Faceting;
import de.fhopf.lucenesolrtalk.web.SearchResultView;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.solr.client.solrj.SolrServerException;
import org.elasticsearch.action.search.SearchResponse;

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

        final SearchResponse response = searcher.search(query.or(""), fqs);
        List<Result> results = searcher.getResults(response);
        Faceting faceting = searcher.getFacets(response);

        StringBuilder currentQuery = new StringBuilder("/elasticsearch?query=");
        currentQuery.append(query.or(""));
        for (String fq : fqs) {
            currentQuery.append("&fq=");
            currentQuery.append(fq);
        }

        return new SearchResultView("/elasticsearch/", query.or(""), results, null, faceting, currentQuery.toString());
    }
}

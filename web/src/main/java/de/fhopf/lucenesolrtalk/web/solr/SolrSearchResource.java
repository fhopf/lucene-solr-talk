package de.fhopf.lucenesolrtalk.web.solr;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import de.fhopf.lucenesolrtalk.web.Faceting;
import de.fhopf.lucenesolrtalk.web.SearchResultView;
import org.apache.solr.client.solrj.SolrServerException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Set;

@Path("/solr")
@Produces("text/html; charset=utf-8")
public class SolrSearchResource {

    private final SolrSearcher searcher;

    public SolrSearchResource(SolrSearcher searcher) {
        this.searcher = searcher;
    }

    @GET
    @Timed
    public SearchResultView search(@QueryParam("query") Optional<String> query, @QueryParam("sort") Optional<String> sort,
                                   @QueryParam("fq") Set<String> fqs) throws SolrServerException {
        SolrSearchResult result = searcher.search(query, fqs);
        Faceting faceting = new Faceting(result.categoryFacet, result.speakerFacet, result.typeFacet);
        StringBuilder currentQuery = new StringBuilder("/solr?query=");
        currentQuery.append(query.or(""));
        for (String fq: fqs) {
            currentQuery.append("&fq=");
            currentQuery.append(fq);
        }
        return new SearchResultView(query.or(""), result.results, null, faceting, currentQuery.toString());
    }

}

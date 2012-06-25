package de.fhopf.lucenesolrtalk.web.solr;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import de.fhopf.Result;
import de.fhopf.lucenesolrtalk.web.Categories;
import de.fhopf.lucenesolrtalk.web.Faceting;
import de.fhopf.lucenesolrtalk.web.SearchResultView;
import org.apache.lucene.queryParser.ParseException;
import org.apache.solr.client.solrj.SolrServerException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
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

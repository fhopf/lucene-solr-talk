package de.fhopf.lucenesolrtalk.web.solr;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import de.fhopf.lucenesolrtalk.web.Facet;
import de.fhopf.lucenesolrtalk.web.Faceting;
import de.fhopf.lucenesolrtalk.web.SearchResultView;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Set;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;

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
        Faceting faceting = new Faceting(transform(result.categoryFacet), transform(result.speakerFacet));
        StringBuilder currentQuery = new StringBuilder("/solr?query=");
        currentQuery.append(query.or(""));
        for (String fq : fqs) {
            currentQuery.append("&fq=");
            currentQuery.append(fq);
        }
        return new SearchResultView("/solr/", query.or(""), result.results, null, faceting, currentQuery.toString());
    }

    private List<Facet> transform(FacetField facetField) {
        List<Facet> facets = new ArrayList<>();
        if (facetField != null) {
            for (Count count : facetField.getValues()) {
                facets.add(new Facet(count.getName(), count.getCount(), facetField.getName()));
            }
        }
        return facets;
    }
}

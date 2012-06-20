package de.fhopf.lucenesolrtalk.web.solr;

import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import de.fhopf.Result;
import de.fhopf.lucenesolrtalk.web.Categories;
import de.fhopf.lucenesolrtalk.web.Faceting;
import de.fhopf.lucenesolrtalk.web.SearchResultView;
import org.apache.lucene.queryParser.ParseException;
import org.apache.solr.client.solrj.SolrServerException;

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
    public SearchResultView search(@QueryParam("query") Optional<String> query, @QueryParam("sort") Optional<String> sort,
                                   @QueryParam("category") Optional<String> category) throws SolrServerException {
        List<Result> results = Collections.emptyList();
        List<String> categoryValues = searcher.getAllCategories();
        SolrSearchResult result = searcher.search(query);
        Categories categories = new Categories(categoryValues, category.or(""));
        Faceting faceting = new Faceting(result.categoryFacet, result.speakerFacet, result.typeFacet);
        return new SearchResultView(query.or(""), result.results, categories, faceting);
    }

}

package de.fhopf.lucenesolrtalk.web.solr;

import com.google.common.base.Optional;
import de.fhopf.Result;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import java.util.*;

import static java.util.Collections.emptyList;

public class SolrSearcher {

    private final SolrServer solrServer;

    public SolrSearcher(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public List<String> getAllCategories() {
        return emptyList();
    }

    public SolrSearchResult search(Optional<String> query) throws SolrServerException {

        if (query.isPresent()) {
            SolrQuery solrQuery = new SolrQuery(query.get());
            QueryResponse response = solrServer.query(solrQuery);

            // TODO move to a function?
            List<Result> results = new ArrayList<Result>();
            for (SolrDocument doc: response.getResults()) {
                results.add(asResult(doc));
            }

            FacetField categoryFacets = response.getFacetField("category");
            FacetField speakerFacets = response.getFacetField("speaker");
            FacetField typeFacets = response.getFacetField("type");

            return new SolrSearchResult(results, categoryFacets, speakerFacets, typeFacets);
        } else {
            return new SolrSearchResult(Collections.<Result>emptyList(), null, null, null);
        }

    }

    private Result asResult(SolrDocument doc) {
        String title = (String) doc.getFieldValue("title");
        // TODO as functions or cast correctly
        List<String> categories = toStrings(doc.getFieldValues("category"));
        List<String> speakers = toStrings(doc.getFieldValues("speaker"));
        Date date = (Date) doc.getFieldValue("date");
        return new Result(title, "", categories, speakers, date);
    }

    private List<String> toStrings(Collection<Object> values) {
        List<String> result = new ArrayList<String>();
        for (Object obj: values) {
            result.add(obj.toString());
        }
        return result;
    }

}

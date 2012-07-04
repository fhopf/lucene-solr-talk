package de.fhopf.lucenesolrtalk.web.solr;

import com.google.common.base.Optional;
import de.fhopf.lucenesolrtalk.Result;
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

    public SolrSearchResult search(Optional<String> query, Set<String> fq) throws SolrServerException {

        if (query.isPresent()) {
            SolrQuery solrQuery = new SolrQuery(query.get());
            solrQuery.setQueryType("/jugka");
            for(String filter: fq) {
                solrQuery.addFilterQuery(filter);
            }
            QueryResponse response = solrServer.query(solrQuery);

            // TODO move to a function?
            List<Result> results = new ArrayList<Result>();
            for (SolrDocument doc: response.getResults()) {
                // highlighting is a map from document id to Field<=>Fragment mapping
                Map<String, List<String>> fragments = Collections.emptyMap();
                if (response.getHighlighting() != null) {
                    fragments = response.getHighlighting().get(doc.getFieldValue("path"));
                }
                Result result = asResult(doc, fragments);
                results.add(result);

            }

            FacetField categoryFacets = response.getFacetField("category");
            FacetField speakerFacets = response.getFacetField("speaker");
            FacetField typeFacets = response.getFacetField("type");

            return new SolrSearchResult(results, categoryFacets, speakerFacets, typeFacets);
        } else {
            return new SolrSearchResult(Collections.<Result>emptyList(), null, null, null);
        }

    }

    private Result asResult(SolrDocument doc, Map<String, List<String>> fragments) {
        String title = (String) doc.getFieldValue("title");
        // TODO as functions or cast correctly
        List<String> categories = toStrings(doc.getFieldValues("category"));
        List<String> speakers = toStrings(doc.getFieldValues("speaker"));
        Date date = (Date) doc.getFieldValue("date");

        List<String> titleFragments = fragments.get("title");
        if (titleFragments != null && !titleFragments.isEmpty()) {
            title = join(titleFragments);
        }

        List<String> contentFragments = fragments.get("content");
        String excerpt = "";
        if (contentFragments != null && !contentFragments.isEmpty()) {
            excerpt = join(contentFragments);
        }

        return new Result(title, excerpt, categories, speakers, date);
    }

    private String join(List<String> tokens) {
        // TODO use existing helper mehod
        StringBuilder builder = new StringBuilder();
        for (String token: tokens) {
            builder.append(token);
            builder.append(" ... ");
        }
        return builder.toString();
    }

    private List<String> toStrings(Collection<Object> values) {
        // TODO add a function
        List<String> result = new ArrayList<String>();
        for (Object obj: values) {
            result.add(obj.toString());
        }
        return result;
    }

}

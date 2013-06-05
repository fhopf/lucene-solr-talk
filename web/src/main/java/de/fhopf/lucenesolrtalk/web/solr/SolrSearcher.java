package de.fhopf.lucenesolrtalk.web.solr;

import com.google.common.base.Optional;
import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.web.Facet;
import de.fhopf.lucenesolrtalk.web.Faceting;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import java.util.*;

import static java.util.Collections.emptyList;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.util.DateUtil;

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
            for (String filter : fq) {
                solrQuery.addFilterQuery(filter);
            }
            QueryResponse response = solrServer.query(solrQuery);

            // TODO move to a function?
            List<Result> results = new ArrayList<Result>();
            for (SolrDocument doc : response.getResults()) {
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
            FacetField organizerFacets = response.getFacetField("organizer");
            RangeFacet.Date dateFacets = null;
            if (!response.getFacetRanges().isEmpty()) {
                // there is only one range facet
                dateFacets = (RangeFacet.Date) response.getFacetRanges().get(0);
            }

            Faceting faceting = new Faceting(transform(categoryFacets), transform(speakerFacets), transform(dateFacets), transform(organizerFacets));
            
            return new SolrSearchResult(results, faceting);
        } else {
            return new SolrSearchResult(Collections.<Result>emptyList(), null);
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
        for (String token : tokens) {
            builder.append(token);
            builder.append(" ... ");
        }
        return builder.toString();
    }

    private List<String> toStrings(Collection<Object> values) {
        // TODO add a function
        List<String> result = new ArrayList<String>();
        if (values != null) {
            for (Object obj : values) {
                result.add(obj.toString());
            }
        }
        return result;
    }

    private List<Facet> transform(FacetField facetField) {
        List<Facet> facets = new ArrayList<>();
        if (facetField != null && facetField.getValues() != null) {
            for (FacetField.Count count : facetField.getValues()) {
                facets.add(Facet.withFilterQuery(count.getName(), count.getCount(), count.getAsFilterQuery()));
            }
        }
        return facets;
    }

    private List<Facet> transform(RangeFacet.Date facetField) {
        // this whole thing feels rather fragile and should be reworked
        List<Facet> facets = new ArrayList<>();
        if (facetField != null && facetField.getCounts() != null) {
            for (RangeFacet.Count count : facetField.getCounts()) {
                try {
                    // for display we are just intersted in the year so take it from the start 
                    Date startDate = DateUtil.parseDate(count.getValue());
                    Calendar start = GregorianCalendar.getInstance();
                    start.setTime(startDate);
                    String label = String.valueOf(start.get(Calendar.YEAR));
                    // create a filter query that spans from start to end
                    // will result in something like this: [XXX TO XXX+1YEAR]
                    StringBuilder fq = new StringBuilder(facetField.getName());
                    fq.append(":[");
                    fq.append(count.getValue());
                    fq.append(" TO ");
                    fq.append(count.getValue());
                    fq.append(facetField.getGap());
                    fq.append("]");
                    facets.add(Facet.withFilterQuery(label, count.getCount(),  URLEncoder.encode(fq.toString(), "UTF-8")));
                } catch (ParseException ex) {
                    throw new IllegalStateException(ex);
                } catch (UnsupportedEncodingException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
        return facets;
    }

}

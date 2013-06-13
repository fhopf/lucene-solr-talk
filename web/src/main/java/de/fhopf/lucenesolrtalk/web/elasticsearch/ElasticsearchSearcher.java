package de.fhopf.lucenesolrtalk.web.elasticsearch;

import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.web.Facet;
import de.fhopf.lucenesolrtalk.web.Faceting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.datehistogram.DateHistogramFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.highlight.HighlightField;

/**
 * Retrieves results from elasticsearch.
 */
public class ElasticsearchSearcher {

    private final Client client;
    static final String INDEX = "bedcon";

    public ElasticsearchSearcher(Client client) {
        this.client = client;
    }

    public SearchResponse search(String token) {
        return search(token, Collections.<String>emptyList());
    }

    public List<Result> getResults(SearchResponse response) {
        List<Result> results = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            Result res = new Result(getTitle(hit), getExcerpt(hit), getCategories(hit), getAuthors(hit), getDate(hit));
            results.add(res);
        }

        return results;
    }

    public Faceting getFacets(SearchResponse response) {

        return new Faceting(buildFacets(response, "category"), buildFacets(response, "speaker"), buildYearFacets(response, "date"), buildFacets(response, "organizer"));
    }

    private List<Facet> buildFacets(SearchResponse response, String name) {
        List<Facet> facets = new ArrayList<>();
        TermsFacet termFacet = response.getFacets().facet(name);
        for (TermsFacet.Entry entry : termFacet.getEntries()) {
            facets.add(Facet.termFacet(entry.getTerm().string(), entry.getCount(), name));
        }
        return facets;
    }
    
    private List<Facet> buildYearFacets(SearchResponse response, String name) {
        List<Facet> facets = new ArrayList<>();
        DateHistogramFacet dateFacet = response.getFacets().facet(name);
        for (DateHistogramFacet.Entry entry : dateFacet.getEntries()) {
            DateTime date = new DateTime(entry.getTime());
            String formattedDate = ISODateTimeFormat.date().print(entry.getTime());
            // building a range query should be seperated from the term filter queries
//            StringBuilder fq = new StringBuilder(name);
//            fq.append(":[");
//            fq.append(formattedDate);
//            fq.append(" TO ");
//            fq.append(formattedDate);
//            fq.append("||+12M/d]");
            facets.add(Facet.withFilterQuery(String.valueOf(date.getYear()), entry.getCount(), ""));
        }
        return facets;
    }
    

    private String getTitle(SearchHit hit) {
        return hit.getFields().get("title").value().toString();
    }

    private String getExcerpt(SearchHit hit) {
        StringBuilder excerptBuilder = new StringBuilder();
        for (Map.Entry<String, HighlightField> highlight : hit.getHighlightFields().entrySet()) {
            for (Text text : highlight.getValue().fragments()) {
                excerptBuilder.append(text.string());
                excerptBuilder.append(" ... ");
            }
        }
        return excerptBuilder.toString();
    }

    private List<String> getCategories(SearchHit hit) {
        return toStringList(hit.getFields().get("category"));
    }

    private List<String> getAuthors(SearchHit hit) {
        return toStringList(hit.getFields().get("speaker"));
    }

    private List<String> toStringList(SearchHitField field) {
        List<String> result = new ArrayList<>();
        if (field != null) {
            for (Object obj : field.values()) {
                result.add(obj.toString());
            }
        }
        return result;
    }

    private Date getDate(SearchHit hit) {
        DateTime parseDateTime = ISODateTimeFormat.dateTimeParser().parseDateTime((String) hit.getFields().get("date").getValue());
        return parseDateTime.toDate();
    }

    public SearchResponse search(String token, Collection<String> filterQueries) {
        QueryBuilder queryBuilder;
        if (token.isEmpty()) {
            queryBuilder = QueryBuilders.matchAllQuery();
        } else {
            queryBuilder = QueryBuilders.queryString(token).useDisMax(true).field("title").field("content").field("speaker");
        }
        if (!filterQueries.isEmpty()) {
            FilterBuilder filter = null;
            for (String fq : filterQueries) {
                FilterBuilder currentFilter = FilterBuilders.queryFilter(QueryBuilders.queryString(fq));
                if (filter == null) {
                    filter = currentFilter;
                } else {
                    filter = FilterBuilders.andFilter(filter, currentFilter);
                }
            }
            queryBuilder = QueryBuilders.filteredQuery(queryBuilder, filter);
        }
        return client.prepareSearch(INDEX).
                addFacet(FacetBuilders.termsFacet("speaker").field("speaker").size(40)).
                addFacet(FacetBuilders.termsFacet("category").field("category")).
                addFacet(FacetBuilders.dateHistogramFacet("date").field("date").interval("year")).
                addFacet(FacetBuilders.termsFacet("organizer").field("organizer")).
                addFields("title", "category", "speaker", "date").
                setSize(100).
                setQuery(queryBuilder).
                addHighlightedField("content", 150, 5).execute().actionGet();
    }
}

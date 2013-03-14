package de.fhopf.lucenesolrtalk.web.elasticsearch;

import de.fhopf.lucenesolrtalk.Result;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.common.lucene.search.Queries;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
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

    public List<Result> search(String token) {
        List<Result> results = new ArrayList<Result>();
        QueryBuilder queryBuilder = null;
        if (token.isEmpty()) {
            queryBuilder = QueryBuilders.matchAllQuery();
        } else {
            queryBuilder = QueryBuilders.queryString(token);
        }
        SearchResponse response = client.prepareSearch(INDEX).
                addFields("title", "category", "speaker", "date").
                setQuery(queryBuilder).
                addHighlightedField("content").execute().actionGet();
        for (SearchHit hit : response.getHits()) {
            Result res = new Result(getTitle(hit), getExcerpt(hit), getCategories(hit), getAuthors(hit), getDate(hit));
            results.add(res);
        }

        return results;
    }

    private String getTitle(SearchHit hit) {
        return hit.getFields().get("title").value().toString();
    }

    private String getExcerpt(SearchHit hit) {
        StringBuilder excerptBuilder = new StringBuilder();
        for (Map.Entry<String, HighlightField> highlight : hit.getHighlightFields().entrySet()) {
            for (Text text: highlight.getValue().fragments()) {
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
        for (Object obj : field.values()) {
            result.add(obj.toString());
        }
        return result;
    }

    private Date getDate(SearchHit hit) {
        DateTime parseDateTime = ISODateTimeFormat.dateTimeParser().parseDateTime((String) hit.getFields().get("date").getValue());
        return parseDateTime.toDate();
    }
}

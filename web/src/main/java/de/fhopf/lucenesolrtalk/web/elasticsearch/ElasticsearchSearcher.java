package de.fhopf.lucenesolrtalk.web.elasticsearch;

import de.fhopf.lucenesolrtalk.Result;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.lucene.search.Queries;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

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
        SearchResponse response = client.prepareSearch(INDEX).setQuery(queryBuilder).execute().actionGet();
        for (SearchHit hit: response.getHits()) {
            Object title = hit.getSource().get("title");
            //SearchHitField title = hit.getFields().get("title");
            Result res = new Result(title.toString(), "", Collections.<String>emptyList(), Collections.<String>emptyList(), null);
            results.add(res);
        }
        
        return results;
    }
}

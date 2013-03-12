package de.fhopf.elasticsearch;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import de.fhopf.elasticsearch.test.ElasticsearchTestNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.*;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.junit.Rule;

/**
 * Execute some core indexing and searching.
 */
public class CoreTest {
    public static final String INDEX = "myindex";
    public static final String TYPE = "document";

    @Rule
    public ElasticsearchTestNode testNode = new ElasticsearchTestNode();

    @Test
    public void indexAndGet() throws IOException {
        testNode.getClient().prepareIndex(INDEX, TYPE, "1")
                .setSource(jsonBuilder().startObject().field("test", "123").endObject()).setRefresh(true)
                .execute()
                .actionGet();

        GetResponse response = testNode.getClient().prepareGet(INDEX, TYPE, "1").execute().actionGet();
        assertThat(response.getSource().get("test")).isEqualTo("123");
    }

    @Test
    public void termsCanBeRetrieved() throws IOException {
        testNode.getClient().prepareIndex(INDEX, TYPE, "1")
                .setSource(jsonBuilder().startObject().field("test", "Hier steht Text").endObject()).setRefresh(true)
                .execute()
                .actionGet();
        testNode.getClient().prepareIndex(INDEX, TYPE, "2")
                .setSource(jsonBuilder().startObject().field("test", "hier noch mehr Texte").endObject()).setRefresh(true)
                .execute()
                .actionGet();

        //TermsStatsFacetBuilder builder = new TermsStatsFacetBuilder("theTestTerms").keyField("test");
        TermsFacetBuilder builder = new TermsFacetBuilder("myTerms").field("test").allTerms(true);
        SearchResponse actionGet = testNode.getClient().prepareSearch(INDEX).addFacet(builder).setQuery(QueryBuilders.matchAllQuery()).setExplain(true).execute().actionGet();
        System.out.println(actionGet);

        // one facet returned
        assertThat(actionGet.facets().facets().size()).isEqualTo(1);
        
        // default analyzer
        TermsFacet facet = actionGet.facets().facet("myTerms");
        assertThat(facet.getTotalCount()).isEqualTo(7);
        
        // Result: seems to be using only lowercasing so let's see if we can switch to german
//  "facets" : {
//    "myTerms" : {
//      "_type" : "terms",
//      "missing" : 0,
//      "total" : 7,
//      "other" : 0,
//      "terms" : [ {
//        "term" : "hier",
//        "count" : 2
//      }, {
//        "term" : "texte",
//        "count" : 1
//      }, {
//        "term" : "text",
//        "count" : 1
//      }, {
//        "term" : "steht",
//        "count" : 1
//      }, {
//        "term" : "noch",
//        "count" : 1
//      }, {
//        "term" : "mehr",
//        "count" : 1
//      } ]
//    }        
    }
    
    @Test
    public void germanTextIsStemmed() throws IOException {
        String text = "Texte";
        String expected = "text";

        // index needs to be created before mapping can be added
        testNode.getClient().admin().indices().prepareCreate(INDEX).execute().actionGet();
        
        String mapping = Resources.toString(getClass().getResource("/test-mapping.json"), Charsets.UTF_8);
        testNode.getClient().admin().indices().preparePutMapping(INDEX).setType(TYPE).setSource(mapping).execute().actionGet();
        
        index("1", "test", text);
        index("2", "test", expected);
        
        // only one term cause it is stemmed
        List<String> terms = getTerms(INDEX, "test");
        assertThat(terms.size()).isEqualTo(1);
        assertThat(terms.get(0)).isEqualTo(expected);
        // two results when searching
        SearchResponse response = testNode.getClient().prepareSearch(INDEX).setQuery(QueryBuilders.termQuery("test", expected)).execute().actionGet();
        assertThat(response.getHits().getTotalHits()).isEqualTo(2);
    }

    private void index(String id, String field, String value) throws IOException {
        testNode.getClient().prepareIndex(INDEX, TYPE, id)
                .setSource(jsonBuilder().startObject().field(field, value).endObject()).setRefresh(true)
                .execute()
                .actionGet();
    }
    
    private List<String> getTerms(String index, String field) {
        TermsFacetBuilder builder = new TermsFacetBuilder("myTerms").field(field).allTerms(true);
        SearchResponse actionGet = testNode.getClient().prepareSearch(index).addFacet(builder).setQuery(QueryBuilders.matchAllQuery()).setExplain(true).execute().actionGet();
        TermsFacet facet = actionGet.facets().facet("myTerms");
        List<String> result = new ArrayList<>();
        for (TermsFacet.Entry entry: facet.entries()) {
            result.add(entry.getTerm());
        }
        return result;
    }
    
    
}

package de.fhopf.lucenesolrtalk.elasticsearch;

import com.google.common.collect.Collections2;
import de.fhopf.lucenesolrtalk.Talk;
import de.fhopf.lucenesolrtalk.TalkFromFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

//import static org.elasticsearch.

/**
 * Indexes files in Solr.
 */
public class Indexer {

    static final String INDEX = "bedcon";
    static final String TYPE = "talk";
    private final Client client;

    public Indexer(Client client) {
        this.client = client;
    }

    public void index(Collection<Talk> talks) throws IOException {
        BulkRequestBuilder bulk = client.prepareBulk();

        for (Talk talk : talks) {
            XContentBuilder sourceBuilder = XContentFactory.jsonBuilder().startObject()
                    .field("path", talk.path)
                    .field("title", talk.title)
                    .field("date", talk.date)
                    .field("content", talk.content)
                    .field("organizer", talk.organizer)
                    .array("category", talk.categories.toArray(new String[0]))
                    .array("speaker", talk.speakers.toArray(new String[0]));
            IndexRequest request = new IndexRequest(INDEX, TYPE).id(talk.path).source(sourceBuilder);
            //node.client().index(request).actionGet();
            bulk.add(request.source(sourceBuilder));
        }
        bulk.execute().actionGet();
    }

    public void prepareIndex() throws IOException {
        boolean indexExists = client.admin().indices().prepareExists(INDEX).execute().actionGet().isExists();
        if (indexExists) {
            // delete the whole index as during development it is likely that the types will change
            //node.client().prepareDeleteByQuery(INDEX).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
            client.admin().indices().prepareDelete(INDEX).execute().actionGet();
        }
        client.admin().indices().prepareCreate(INDEX).execute().actionGet();
        // TODO how to make the german analyzer the default?
        XContentBuilder builder = XContentFactory.jsonBuilder().
                startObject().
                    startObject(TYPE).
                        startObject("properties").
                            startObject("path").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject().
                            startObject("title").field("type", "string").field("store", "yes").field("analyzer", "german").endObject().
                            startObject("category").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject().
                            startObject("speaker").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject().
                            startObject("date").field("type", "date").field("store", "yes").field("index", "not_analyzed").endObject().
                            startObject("content").field("type", "string").field("store", "yes").field("analyzer", "german").field("term_vector", "with_positions_offsets").endObject().
                            startObject("organizer").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject().
                        endObject().
                    endObject().
                endObject();
        
        client.admin().indices().preparePutMapping(INDEX).setType(TYPE).setSource(builder).execute().actionGet();
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java " + Indexer.class.getCanonicalName() + " <dataDir>");
            System.exit(-1);
        }

        List<File> files = Arrays.asList(new File(args[0]).listFiles());

        Collection<Talk> talks = Collections2.transform(files, new TalkFromFile());

        ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", "false");
        Node client = NodeBuilder.nodeBuilder().client(true).settings(elasticsearchSettings).node();

        Indexer indexer = new Indexer(client.client());
        indexer.prepareIndex();
        indexer.index(talks);
        client.close();
    }
}

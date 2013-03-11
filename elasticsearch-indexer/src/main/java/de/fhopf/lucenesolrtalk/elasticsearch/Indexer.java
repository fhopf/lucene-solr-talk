package de.fhopf.lucenesolrtalk.elasticsearch;

import com.google.common.collect.Collections2;
import de.fhopf.lucenesolrtalk.Talk;
import de.fhopf.lucenesolrtalk.TalkFromFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * Indexes files in Solr.
 */
public class Indexer {

    private final Node node;
    private static final String INDEX = "bedcon";
    private static final String TYPE = "talk";

    public Indexer(Node node) {
        this.node = node;
    }

    public void index(Collection<Talk> talks) throws IOException {
        BulkRequestBuilder bulk = node.client().prepareBulk();
        
        for (Talk talk: talks) {
            XContentBuilder sourceBuilder = XContentFactory.jsonBuilder().startObject()
                                            .field("path", talk.path)
                                            .field("title", talk.title)
                                            .field("date", talk.date)
                                            .field("content", talk.content)
                                            .field("category", talk.categories)
                                            .field("speaker", talk.speakers);
            IndexRequest request = new IndexRequest(INDEX, TYPE).source(sourceBuilder);
            //node.client().index(request).actionGet();
            bulk.add(request.source(sourceBuilder));
        }
        bulk.execute().actionGet();
    }
    
    public void prepareIndex() {
        boolean indexExists = node.client().admin().indices().prepareExists(INDEX).execute().actionGet().exists();
        if (indexExists) {
            node.client().prepareDeleteByQuery(INDEX).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
        } else {
            // TODO add mapping for german
            node.client().admin().indices().prepareCreate(INDEX).execute().actionGet();
        }
    }

    public static void main(String [] args) throws IOException  {

        if (args.length != 1) {
            System.out.println("Usage: java " + Indexer.class.getCanonicalName() + " <dataDir>");
            System.exit(-1);
        }

        List<File> files = Arrays.asList(new File(args[0]).listFiles());

        Collection<Talk> talks = Collections2.transform(files, new TalkFromFile());
        
        ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", "false");
        Node client = NodeBuilder.nodeBuilder().client(true).settings(elasticsearchSettings).node();
        
        Indexer indexer = new Indexer(client);
        indexer.prepareIndex();
        indexer.index(talks);
        client.close();
    }

}

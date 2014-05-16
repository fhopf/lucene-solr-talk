package de.fhopf.lucenesolrtalk.jest;

import com.google.common.collect.Collections2;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Bulk.Builder;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Indexes data in a local Elasticsearch instance using JEST.
 *
 * @author Florian Hopf, http://blog.florian-hopf.de
 */
public class Indexer {

    private final JestClient client;
    private final Log log = LogFactory.getLog(Indexer.class);

    public Indexer(JestClient client) {
        this.client = client;
    }

    public void prepareIndex() throws Exception {
        // delete the index if it exists
        boolean indexExists = client.execute(new IndicesExists.Builder("jug").build()).isSucceeded();
        if (indexExists) {
            client.execute(new DeleteIndex.Builder("jug").build());
        }
        client.execute(new CreateIndex.Builder("jug").build());

        // add the mapping
        String mapping = "\"talk\": { "
                .concat("   \"properties\" : {")
                .concat("       \"path\" : { \"type\" : \"string\", \"index\" : \"not_analyzed\" },")
                .concat("       \"title\" : { \"type\" : \"string\", \"analyzer\" : \"german\" },")
                .concat("       \"category\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\" },")
                .concat("       \"speaker\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\" },")
                .concat("       \"date\" : {\"type\" : \"date\" },")
                .concat("       \"content\" : { \"type\" : \"string\", \"analyzer\" : \"german\" },")
                .concat("       \"organizer\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\" }")
                .concat("} }");
        log.info(mapping);
        PutMapping.Builder putMapping = new PutMapping.Builder("jug", "talk", mapping);
        client.execute(putMapping.build());
    }

    public void index(Collection<Talk> talks) throws Exception {
        Builder bulkIndexBuilder = new Bulk.Builder();
        for (Talk talk : talks) {
            bulkIndexBuilder.addAction(new Index.Builder(talk).index("jug").type("talk").build());
        }
        client.execute(bulkIndexBuilder.build());
    }

    protected void exampleSearch() throws Exception {
        String query = "{\n"
                + "    \"query\": {\n"
                + "        \"filtered\" : {\n"
                + "            \"query\" : {\n"
                + "                \"query_string\" : {\n"
                + "                    \"query\" : \"java\"\n"
                + "                }\n"
                + "            }"
                + "        }\n"
                + "    }\n"
                + "}";
        Search.Builder searchBuilder = new Search.Builder(query).addIndex("jug").addType("talk");
        SearchResult result = client.execute(searchBuilder.build());
        List<Hit<Talk, Void>> hits = result.getHits(Talk.class);
        log.info("Retrieved result " + result.getJsonString());
        for (Hit<Talk, Void> hit: hits) {
            Talk talk = hit.source;
            log.info(talk.getTitle());
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java <classname> <dataDir> <es-url>");
            System.exit(-1);
        }

        List<File> files = Arrays.asList(new File(args[0]).listFiles());
        Collection<Talk> talks = Collections2.transform(files, new TalkFromFile());

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(args[1])
                .multiThreaded(true)
                .build());
        JestClient client = factory.getObject();
        Indexer indexer = new Indexer(client);
        indexer.prepareIndex();
        indexer.index(talks);
        
        // TODO replace with a refresh on the index
        Thread.sleep(2000);
        
        indexer.exampleSearch();
    }
}

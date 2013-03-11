package de.fhopf.lucenesolrtalk.solr;

import com.google.common.collect.Collections2;
import de.fhopf.lucenesolrtalk.Talk;
import de.fhopf.lucenesolrtalk.TalkFromFile;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Indexes files in Solr.
 */
public class Indexer {

    private final SolrServer server;

    public Indexer(SolrServer server) {
        this.server = server;
    }

    public void index(Collection<Talk> talks) throws IOException, SolrServerException {
        // TODO could also be transformed using guava
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        for (Talk talk: talks) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("path", talk.path);
            doc.addField("title", talk.title);
            doc.addField("date", talk.date);
            doc.addField("content", talk.content);
            for (String category: talk.categories) {
                doc.addField("category", category);
            }
            for (String speaker: talk.speakers) {
                doc.addField("speaker", speaker);
            }
            docs.add(doc);
        }

        server.add(docs);
        server.commit();
    }
    
    public void clearIndex() throws SolrServerException, IOException {
        server.deleteByQuery("*:*");
    }

    public static void main(String [] args) throws IOException, SolrServerException {

        if (args.length != 2) {
            System.out.println("Usage: java " + Indexer.class.getCanonicalName() + " <solrUrl> <dataDir>");
            System.exit(-1);
        }

        // could also be passed in
        // TODO use HttpSolrServer
        SolrServer server = new CommonsHttpSolrServer(args[0]);

        List<File> files = Arrays.asList(new File(args[1]).listFiles());

        Collection<Talk> talks = Collections2.transform(files, new TalkFromFile());

        Indexer indexer = new Indexer(server);
        indexer.clearIndex();
        indexer.index(talks);
    }

}

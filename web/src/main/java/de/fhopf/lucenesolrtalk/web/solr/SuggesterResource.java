package de.fhopf.lucenesolrtalk.web.solr;

import org.apache.solr.client.solrj.SolrServer;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@Path("/suggestion")
@Produces(MediaType.APPLICATION_JSON)
public class SuggesterResource {

    private final SolrServer solrServer;

    public SuggesterResource(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public List<String> getSuggestions(String prefix) {
        // TODO call suggester
        return Collections.<String>emptyList();
    }
}

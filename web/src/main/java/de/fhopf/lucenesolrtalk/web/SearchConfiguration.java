package de.fhopf.lucenesolrtalk.web;

import com.yammer.dropwizard.config.Configuration;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Main configuration for the search service.
 */
public class SearchConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    public String indexDir;

    @NotEmpty
    @JsonProperty
    public String solrUrl;

}

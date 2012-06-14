package de.fhopf.lucenesolrtalk.web;

import com.yammer.dropwizard.config.Configuration;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;

/**
 * Main configuration for the search service.
 */
public class LuceneSearchConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String indexDir;

    public LuceneSearchConfiguration(String indexDir) {
        this.indexDir = indexDir;
    }

    public String getIndexDir() {
        return indexDir;
    }
}

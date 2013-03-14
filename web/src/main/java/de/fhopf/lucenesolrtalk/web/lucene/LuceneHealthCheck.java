package de.fhopf.lucenesolrtalk.web.lucene;

import com.yammer.metrics.core.HealthCheck;
import java.io.File;

/**
 *
 */
public class LuceneHealthCheck extends HealthCheck {
    private final String indexDir;

    public LuceneHealthCheck(String indexDir) {
        super("Lucene");
        this.indexDir = indexDir;
    }
    
    @Override
    protected Result check() throws Exception {
        if (new File(indexDir).exists()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Index dir " + indexDir + " doesn't exist");
        }
    }
    
}

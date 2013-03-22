package de.fhopf.elasticsearch.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.rules.ExternalResource;

/**
 * A JUnit rule that creates an elasticsearch client that writes to a temp dir.
 * Inspired by http://cupofjava.de/blog/2012/11/27/embedded-elasticsearch-server-for-tests/
 * @author Florian Hopf
 */
public class ElasticsearchTestNode extends ExternalResource {

    private Node node;
    private Path dataDirectory; 
    
    @Override
    protected void before() throws Throwable {
        try {
            dataDirectory = Files.createTempDirectory("es-test", new FileAttribute<?> []{});
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", "false")
                .put("path.data", dataDirectory.toString());

        node = NodeBuilder.nodeBuilder()
                .local(true)
                .settings(elasticsearchSettings.build())
                .node();
    }

    @Override
    protected void after() {
        node.close();
        try {
            FileUtils.deleteDirectory(dataDirectory.toFile());
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    public Client getClient() {
        return node.client();
    }       
    
}

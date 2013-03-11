package de.fhopf.elasticsearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * Embedded Server, inspired by http://cupofjava.de/blog/2012/11/27/embedded-elasticsearch-server-for-tests/
 */
public class EmbeddedElasticsearchServer {
    
    private final Node node;
    private Path dataDirectory; 

    public EmbeddedElasticsearchServer() {
        this(null);
    }
    
    public EmbeddedElasticsearchServer(String elastichome) {
        try {
            dataDirectory = Files.createTempDirectory("es-test", new FileAttribute<?> []{});
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", "false")
                .put("path.data", dataDirectory.toString());

        if (elastichome != null) {
            elasticsearchSettings = elasticsearchSettings.put("path.home", elastichome);
        }
        
        node = NodeBuilder.nodeBuilder()
                .local(true)
                .settings(elasticsearchSettings.build())
                .node();
    }

    public Client getClient() {
        return node.client();
    }

    public void shutdown() {
        node.close();
        try {
            deleteDataDirectory();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void deleteDataDirectory() throws IOException {
        FileUtils.deleteDirectory(dataDirectory.toFile());
    }
    
}

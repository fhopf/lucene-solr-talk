package de.fhopf.lucene;

import com.google.common.base.Optional;
import de.fhopf.Talk;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Indexes files where the content is extracted by Tika.
 */
public class TikaIndexer {

    private final Indexer indexer;

    private final Logger logger = LoggerFactory.getLogger(TikaIndexer.class);

    public TikaIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public void indexDir(String path)  {
        File [] files = new File(path).listFiles();
        AutoDetectParser parser = new AutoDetectParser();

        logger.debug("Found {} files in dir", files.length);

        List<Talk> talks = new ArrayList<Talk>();

        for (File file: files) {
            Metadata metadata = new Metadata();
            metadata.add(Metadata.RESOURCE_NAME_KEY, file.getName());
            BodyContentHandler contentHandler = new BodyContentHandler();
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                parser.parse(in, contentHandler, metadata);
                logMetadata(metadata);
                Optional<String> title = Optional.fromNullable(metadata.get(Metadata.TITLE));
                Optional<String> speaker = Optional.fromNullable(metadata.get(Metadata.AUTHOR));
                String content = contentHandler.toString();
                // TODO date
                Date date = new Date();
                Talk talk = new Talk(file.getAbsolutePath(), title.or(file.getName()), Arrays.asList(speaker.or("")), date, contentHandler.toString(),
                        Collections.<String>emptyList());
                talks.add(talk);
            } catch (FileNotFoundException e) {
                logger.warn(e.getMessage(), e);
            } catch (SAXException e) {
                logger.warn(e.getMessage(), e);
            } catch (TikaException e) {
                logger.warn(e.getMessage(), e);
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            } finally {
                Utils.close(in);
            }
        }

        logger.debug("Extracted {} talks", talks.size());

        indexer.index(talks.toArray(new Talk[talks.size()]));

    }

    private void logMetadata(Metadata metadata) {
        for (String name: metadata.names()) {
            System.err.println(name + ": " + metadata.get(name));
            logger.error("{}: {}", name, metadata.get(name));
        }
    }

    public static void main(String [] args) throws IOException {
        if (args.length != 2) {
            System.out.println(String.format("Usage: java %s <indexdir> <datadir>", TikaIndexer.class.getCanonicalName()));
            System.exit(1);
        }

        Directory dir = FSDirectory.open(new File(args[0]));
        Indexer indexer = new Indexer(dir);
        TikaIndexer tikaIndexer = new TikaIndexer(indexer);
        tikaIndexer.indexDir(args[1]);

    }


}

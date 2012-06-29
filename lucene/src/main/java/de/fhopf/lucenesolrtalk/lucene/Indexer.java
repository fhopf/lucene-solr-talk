package de.fhopf.lucenesolrtalk.lucene;

import com.google.common.collect.Collections2;
import de.fhopf.lucenesolrtalk.Talk;
import de.fhopf.lucenesolrtalk.TalkFromFile;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Indexes talks in Lucene.
 */
public class Indexer {

    private Logger logger = LoggerFactory.getLogger(Indexer.class);

    private final Directory directory;

    public Indexer(Directory directory) {
        this.directory = directory;
    }

    public void index(Talk...talks) {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, new GermanAnalyzer(Version.LUCENE_36));
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory, config);
            for (Talk talk: talks) {
                logger.info(String.format("Indexing talk %s", talk.title));
                writer.addDocument(asDocument(talk));
            }
            writer.commit();
        } catch (IOException ex) {
            if (writer != null) {
                try {
                    writer.rollback();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void indexDirectory(String path) {
        Collection<File> paths = Arrays.asList(new File(path).listFiles());
        Collection<Talk> talks = Collections2.transform(paths, new TalkFromFile());
        logger.info("Indexing {} talks", talks.size());
        index(talks.toArray(new Talk[talks.size()]));
    }

    private Document asDocument(Talk talk) {
        Document doc = new Document();
        for (String speaker: talk.speakers) {
            doc.add(new Field("speaker", speaker, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
        }
        doc.add(new Field("title", talk.title, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("path", talk.path, Field.Store.YES, Field.Index.NO));
        doc.add(new Field("content", talk.content, Field.Store.NO, Field.Index.ANALYZED));
        for (String category: talk.categories) {
            doc.add(new Field("category", category, Field.Store.YES, Field.Index.NOT_ANALYZED));
        }
        doc.add(new Field("date", DateTools.timeToString(talk.date.getTime(), DateTools.Resolution.DAY), Field.Store.YES, Field.Index.NOT_ANALYZED));

        String all = extractAllText(talk);
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));

        return doc;
    }

    private String extractAllText(Talk talk) {
        StringBuilder allText = new StringBuilder(talk.title);
        for (String category: talk.categories) {
            allText.append(" ");
            allText.append(category);
        }
        for (String speaker: talk.speakers) {
            allText.append(" ");
            allText.append(speaker);
        }
        allText.append(" ");
        allText.append(talk.content);
        return allText.toString();
    }

    public static void main(String [] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java " + Indexer.class.getCanonicalName() + " <indexDir> <dataDir>");
        }

        Directory dir = FSDirectory.open(new File(args[0]));
        Indexer indexer = new Indexer(dir);
        indexer.indexDirectory(args[1]);
    }
}

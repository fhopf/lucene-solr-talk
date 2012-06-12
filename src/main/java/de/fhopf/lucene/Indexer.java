package de.fhopf.lucene;

import de.fhopf.Talk;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.IOException;

/**
 * Indexes talks in Lucene.
 */
public class Indexer {

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

    private Document asDocument(Talk talk) {
        Document doc = new Document();
        doc.add(new Field("author", talk.author, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));
        doc.add(new Field("title", talk.title, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("path", talk.path, Field.Store.YES, Field.Index.NO));
        doc.add(new Field("content", talk.content, Field.Store.NO, Field.Index.ANALYZED));
        return doc;
    }
}

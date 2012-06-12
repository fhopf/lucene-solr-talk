package de.fhopf.lucene;

import de.fhopf.Talk;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.ParallelReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 */
public class IndexerTest {

    private Directory directory;
    private Indexer indexer;

    @Before
    public void initIndexer() {
        directory = new RAMDirectory();
        indexer = new Indexer(directory);
    }

    @Test
    public void twoTalksAreIndexed() throws IOException {
        Talk talk1 = new Talk("/path/to/talk1", "Title 1", "Author 1", new Date(), "Contents");
        Talk talk2 = new Talk("/path/to/talk2", "Title 2", "Author 2", new Date(), "More Contents");

        indexer.index(talk1, talk2);
        IndexReader reader = IndexReader.open(directory);
        assertEquals(2, reader.numDocs());
        reader.close();
    }

}

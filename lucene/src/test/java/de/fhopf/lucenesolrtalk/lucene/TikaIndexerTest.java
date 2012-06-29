package de.fhopf.lucenesolrtalk.lucene;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.Talk;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TikaIndexerTest {

    private static String dataDir;

    @BeforeClass
    public static void copySlides() throws IOException {
        File directory = Files.createTempDir();
        File pdf = new File(directory, "enter-the-gradle.pdf");
        FileOutputStream out = new FileOutputStream(pdf);
        Resources.copy(Resources.getResource(TikaIndexerTest.class, "enter-the-gradle.pdf"), out);
        out.close();
        dataDir = directory.getAbsolutePath();
    }


    @Test
    public void pdfIsTransformed() throws IOException {

        Indexer indexer = mock(Indexer.class);
        TikaIndexer tikaIndexer = new TikaIndexer(indexer);

        tikaIndexer.indexDir(dataDir);

        ArgumentCaptor<Talk> argument = ArgumentCaptor.forClass(Talk.class);
        verify(indexer).index(argument.capture());

        assertEquals("enter-the-gradle.pdf", argument.getValue().title);
        assertFalse(argument.getValue().content.isEmpty());
    }


    @Test
    public void gradleIsGroovy() throws ParseException {
        Directory dir = new RAMDirectory();
        Indexer indexer = new Indexer(dir);
        TikaIndexer tikaIndexer = new TikaIndexer(indexer);

        tikaIndexer.indexDir(dataDir);

        Searcher searcher = new Searcher(dir);

        List<Result> results = searcher.search("Groovy");

        assertEquals(1, results.size());

    }

    @Test
    public void parseWithTika() throws IOException, SAXException, TikaException {
        // used for the slides, contains some duplication with TikaIndexer
        File file = new File(dataDir, "enter-the-gradle.pdf");

        FileInputStream in = new FileInputStream(file);
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        metadata.add(Metadata.RESOURCE_NAME_KEY, file.getName());
        BodyContentHandler contentHandler = new BodyContentHandler();

        parser.parse(in, contentHandler, metadata);

        String title = metadata.get(Metadata.TITLE);
        String author = metadata.get(Metadata.AUTHOR);
        String content = contentHandler.toString();


        assertTrue(content.contains("Groovy"));
        in.close();
    }

}

package de.fhopf.lucenesolrtalk.lucene;

import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import de.fhopf.Result;
import de.fhopf.Talk;
import de.fhopf.lucene.Indexer;
import de.fhopf.lucene.Searcher;
import de.fhopf.lucene.TikaIndexer;
import de.fhopf.lucene.Utils;
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

import java.io.*;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

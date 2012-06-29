package de.fhopf.lucenesolrtalk;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 */
public class TalkFromFileTest {

    private static String path;
    private TalkFromFile talkFromFile = new TalkFromFile();

    @BeforeClass
    public static void copyToTmpFolder() throws IOException {
        Path pathRepresentation = Files.createTempFile("talkfromfiletest", ".properties");
        path = pathRepresentation.toString();
        Files.copy(TalkFromFileTest.class.getResourceAsStream("post.properties"), pathRepresentation, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void valuesAreSet() throws ParseException {
        Talk talk = talkFromFile.apply(new File(path));
        assertEquals("Florian Hopf", talk.speakers.get(0));
        assertEquals("Titel", talk.title);
        assertEquals(path, talk.path);
        assertEquals("Inhalt", talk.content);

        Date date = new SimpleDateFormat("dd.MM.yyyy").parse("12.06.2012");
        assertEquals(date, talk.date);

    }

}

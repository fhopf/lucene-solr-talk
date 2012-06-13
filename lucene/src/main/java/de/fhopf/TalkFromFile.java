package de.fhopf;

import com.google.common.base.Function;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Function that reads a talk from a properties file.
 */
public class TalkFromFile implements Function<File, Talk> {

    private DateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public Talk apply(File input) {
        Properties props = read(input);
        String speakerValue = props.getProperty("speaker");
        String title = props.getProperty("title");
        String dateValue = props.getProperty("date");
        String contents = props.getProperty("content");
        String categoriesValue = props.getProperty("categories");

        List<String> speakers = Arrays.asList(speakerValue.split(","));
        List<String> categories = Arrays.asList(categoriesValue.split(","));

        return new Talk(input.getAbsolutePath(), title, speakers, parseDate(dateValue), contents, categories);
    }

    private Date parseDate(String dateValue) {
        try {
            return format.parse(dateValue);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    private Properties read(File input) {
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(input);
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }
}

package de.fhopf;

import com.google.common.base.Function;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Function that reads a talk from a properties file.
 */
public class TalkFromFile implements Function<String, Talk> {

    private DateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public Talk apply(String input) {
        Properties props = read(input);
        String author = props.getProperty("author");
        String title = props.getProperty("title");
        String dateValue = props.getProperty("date");
        String contents = props.getProperty("content");

        return new Talk(input, title, author, parseDate(dateValue), contents);
    }

    private Date parseDate(String dateValue) {
        try {
            return format.parse(dateValue);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    private Properties read(String input) {
        Properties props = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(input));
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

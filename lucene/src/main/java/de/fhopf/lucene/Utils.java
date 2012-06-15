package de.fhopf.lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.IndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static final void close(IndexReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    public static final void logTermDictionary(IndexReader reader) throws IOException {
        TermEnum terms = reader.terms();
        while(terms.next()) {
            Term term = terms.term();
            logger.info(String.format("%s: %s", term.field(), term.text()));
        }
    }


    public static void close(IndexSearcher searcher) {
        if (searcher != null) {
            try {
                searcher.close();
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    public static void close(Closeable in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }
}

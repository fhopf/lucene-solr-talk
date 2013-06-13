package de.fhopf.lucenesolrtalk.lucene;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import de.fhopf.lucenesolrtalk.Result;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Searches on an index.
 *
 * Note: When used from a webapp this implementation is not as efficient as it should be. On each request it opens
 * a new IndexReader which can be quite costly. If you are interested in how to implement a SearchManager that takes
 * care of opening and updating IndexReaders have a look at Chapter 11 of Lucene in Action.
 */
public class Searcher {

    private Logger logger = LoggerFactory.getLogger(Searcher.class);

    private final Directory directory;

    public Searcher(Directory directory) {
        this.directory = directory;
    }

    private List<Result> search(Query query, Optional<Sort> sort) {
        IndexSearcher searcher = null;
        try {
            searcher = new IndexSearcher(DirectoryReader.open(directory));
            List<Result> result = new ArrayList<>();
            TopDocs topDocs = searcher.search(query, 10, sort.or(Sort.RELEVANCE));
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                result.add(asResult(doc, query));
            }
            return result;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        } finally {
            Utils.close(searcher);
        }
    }

    private Result asResult(Document doc, Query query) {
        String title = doc.get("title");
        String excerpt = extractExcerpt(doc, query);

        List<String> speakers = Arrays.asList(doc.getValues("speaker"));
        List<String> categories = Arrays.asList(doc.getValues("category"));
        Date date = null;
        try {
            date = DateTools.stringToDate(doc.get("date"));
        } catch (java.text.ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return new Result(title, excerpt, categories, speakers, date);
    }

    private String extractExcerpt(Document doc, Query query) {
        TokenStream stream = TokenSources.getTokenStream("content", doc.get("content"),
                new GermanAnalyzer(Version.LUCENE_43));
        QueryScorer scorer = new QueryScorer(query, "content");
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 100);

        Highlighter highlighter = new Highlighter(scorer);
        highlighter.setTextFragmenter(fragmenter);

        String excerpt = "";
        String content = doc.get("content");

        try {
            String[] fragments = highlighter.getBestFragments(stream, content, 5);
            Joiner joiner = Joiner.on("...");
            excerpt = joiner.join(fragments);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (InvalidTokenOffsetsException e) {
            logger.error(e.getMessage(), e);
        }
        // TODO add the beginning of the content as excerpt if it's not available

        return excerpt;
    }

    public List<Result> search(String query) throws ParseException {
        return search(query, Optional.<Sort>absent());
    }

    private List<Result> search(String query, Optional<Sort> sort) throws ParseException {
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_43, new String[] {"title", "content", "category", "speaker"}, new GermanAnalyzer(Version.LUCENE_36));
        Query actualQuery = queryParser.parse(query);
        logger.info("Searching for {}", query);
        return search(actualQuery, sort);
    }

    public List<Result> searchSortedByDate(String query) throws ParseException {
        SortField field = new SortField("date", SortField.Type.STRING, true);
        Sort sort = new Sort(field);
        return search(query, Optional.of(sort));
    }

    public static void main(String[] args) throws IOException, ParseException {
        if (args.length != 2) {
            System.out.println(String.format("Usage: java %s <indexdir> <query>", Searcher.class.getCanonicalName()));
            System.exit(0);
        }

        Directory dir = FSDirectory.open(new File(args[0]));
        Searcher searcher = new Searcher(dir);
        List<Result> results = searcher.search(args[1]);
        System.out.println(String.format("Found %d results", results.size()));
        for (Result result: results) {
            System.out.println(String.format("%s", result.getTitle()));
        }
    }
}

package de.fhopf.lucenesolrtalk.lucene;

import com.google.common.base.Optional;
import de.fhopf.lucenesolrtalk.Result;
import de.fhopf.lucenesolrtalk.Talk;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import org.apache.lucene.queryparser.classic.ParseException;

public class SearcherTest {

    private Indexer indexer;
    private Searcher searcher;

    @Before
    public void initIndexerAndSearcher() {
        Directory dir = new RAMDirectory();
        indexer = new Indexer(dir);
        searcher = new Searcher(dir);
    }

    @Test
    public void searchAuthor() throws ParseException {
        Talk talk1 = newAuthorTalk("Florian Hopf");
        Talk talk2 = newAuthorTalk("Florian");
        indexer.index(talk1, talk2);
        List<Result> documents = searcher.search("speaker:Florian");
        assertEquals(2, documents.size());
        documents = searcher.search("speaker:\"Florian Hopf\"");
        assertEquals(1, documents.size());
    }

    @Test
    public void sortedByDate() throws ParseException {
        Date now = new Date();
        Calendar cal = GregorianCalendar.getInstance();
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = cal.getTime();
        Talk yesterdayMoreRelevant = new Talk("", "Titel Treffer", new ArrayList<String>(), yesterday, "", new ArrayList<String>(), "");
        Talk today = new Talk("", "Titel", new ArrayList<String>(), now, "", new ArrayList<String>(), "");
        indexer.index(yesterdayMoreRelevant, today);

        // sanity check
        List<Result> result = searcher.search("Titel Treffer");
        assertEquals(2, result.size());
        assertEquals("Titel Treffer", result.get(0).getTitle());

        // sort
        result = searcher.searchSortedByDate("Titel");
        assertEquals(2, result.size());
        assertEquals("Titel", result.get(0).getTitle());

    }

    @Test
    public void searchWithHighlighter() throws ParseException {
        StringBuilder content = new StringBuilder("Hier taucht das Wort auf ");
        for (int i = 0; i < 10; i++) {
            content.append(" und dazwischen ist ganz viel Text der das nicht enthält, ");
        }
        content.append("aber jetzt kommt das Wort nochmal.");

        Talk talk = new Talk("", "", Collections.<String>emptyList(), new Date(), content.toString(), Collections.<String>emptyList(), "");
        indexer.index(talk);

        List<Result> result = searcher.search("Wort");
        assertEquals(1, result.size());
        assertTrue(result.get(0).getExcerpt(), result.get(0).getExcerpt().contains("<B>Wort</B>"));
        assertTrue(result.get(0).getExcerpt(), result.get(0).getExcerpt().contains("..."));
    }

    @Test
    public void phraseQueryOnStemmedTitle() throws ParseException {
        Talk talk = new Talk("", "Verteiltes Suchen mit Elasticsearch", Collections.<String>emptyList(), new Date(), "", Collections.<String>emptyList(), "");
        indexer.index(talk);

        // phrase queries do match even though the terms are stemmed
        List<Result> result = searcher.search("title:\"verteilte suche\"");
        assertEquals(1, result.size());
    }
    
    private Talk newAuthorTalk(String author) {
        return new Talk("", "", Arrays.asList(author), new Date(), "", new ArrayList<String>(), "");
    }

    private Talk newCategoryTalk(String... category) {
        return new Talk("", "", new ArrayList<String>(), new Date(), "", Arrays.asList(category), "");
    }

}

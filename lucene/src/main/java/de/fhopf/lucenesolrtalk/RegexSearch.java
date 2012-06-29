package de.fhopf.lucenesolrtalk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Searches for occurences of a given term in all text files in a directory.
 * Accepts two parameters: First the search term and an optional directory. If
 * the directory is not given the current directory is used.
 *
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class RegexSearch {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("usage: java RegexSearch term [dir]");
            System.exit(0);
        }

        String term = args[0];

        String dir = new File("").getParent();

        if (args.length > 1) {
            dir = args[1];
        }

        List<String> hits = search(term, dir);
        if (hits.isEmpty()) {
            System.out.println("No hits");
        } else {
            for (String hit : hits) {
                System.out.println("Found match in " + hit);
            }
        }

    }

    public static List<String> search(String term, String dir) {
        List<String> filesWithMatches = new ArrayList<String>();

        File directory = new File(dir);
        File[] textFiles = directory.listFiles(new TextFiles());

        for (File probableMatch : textFiles) {
            System.out.println("Checking " + probableMatch.getAbsolutePath());
            String text = readText(probableMatch);
            if (text.matches(".*" + Pattern.quote(term) + ".*")) {
                filesWithMatches.add(probableMatch.getAbsolutePath());
            }
        }

        return filesWithMatches;
    }

    private static String readText(File probableMatch) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(probableMatch));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(File.separator);
            }
            reader.close();
            return builder.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static class TextFiles implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".txt");
        }
    }
}

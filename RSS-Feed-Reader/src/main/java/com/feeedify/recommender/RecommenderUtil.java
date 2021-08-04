package com.feeedify.recommender;

import static com.feeedify.constants.Constants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommenderUtil {

    public static List<String> cleanDocument(String doc, String language) {
        Set<String> stopwords = loadStopWords(language);
        String[] words = doc.replaceAll("\\p{Punct}", "").toLowerCase().split("\\s");
        List<String> wordList = new ArrayList<String>();
        for (String word : words) {
            word = word.trim();
            if (word.length() > 0 && !stopwords.contains(word)) {
                wordList.add(word);
            }
        }
        return wordList;
    }

    /**
     * Load stopwords from a file
     * 
     * @param filename
     * @return
     */
    private static Set<String> loadStopWords(String language) {
        Set<String> stoplist = new HashSet<String>();
        String filename = "";

        switch (language) {
            case "ENGLISH":
                filename = ENGLISH_STOPWORDS;
                break;
            case "GERMAN":
                filename = GERMAN_STOPWORDS;
                break;
            default:
                return stoplist;
        }

        try {
            InputStream in = RecommenderUtil.class.getResourceAsStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                stoplist.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stoplist;
    }
}

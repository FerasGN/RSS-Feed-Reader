package de.htwsaar.pib2021.rss_feed_reader.rest.service;


import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class LanguageIdentifierService {

    //A list of a Language which our database indexing supports
    final private List<String> avaliableLanguages = new ArrayList<String>(
            Arrays.asList("DANISH","DUTCH","ENGLISH","FINNISH","FRENCH","GERMAN","HUNGARIAN","ITALIAN","NORWEGIAN","PORTUGUESE","ROMANIAN","RUSSIAN",
                    "SPANISH","SWEDISH","TURKISH"));

    //Creating the detector
    final LanguageDetector detector = LanguageDetectorBuilder.fromAllSpokenLanguages().withPreloadedLanguageModels().build();

    /**
     * This methode takes a String and detects the language.
     * The String need to be at least 5 characters long
     * @param sample String which should be detected
     * @return String which is the detected language (all uppercase)
     */
    public String searchLanguage(String sample) {
        Language detectedLanguage = detector.detectLanguageOf(sample);
        if(avaliableLanguages.contains(detectedLanguage.toString())){
            return detectedLanguage.toString();
        }
        return"SIMPLE";
    }
}

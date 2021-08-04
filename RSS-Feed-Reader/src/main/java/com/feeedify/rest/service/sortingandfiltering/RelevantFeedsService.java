package com.feeedify.rest.service.sortingandfiltering;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.User;
import com.feeedify.database.repository.RecentlyReadFeedItemUserRepository;
import com.feeedify.recommender.RecommenderUtil;
import com.feeedify.recommender.TF_IDF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelevantFeedsService {

    @Autowired
    private RecentlyReadFeedItemUserRepository recentlyReadFeedItemUserRepository;

    public List<FeedItemUser> findFeedItemsUserOrderedByRelevance(User user, List<FeedItemUser> feedsToSort) {
        List<String> query = createSortingByRelevanceQuery(user);

        List<List<String>> docs = new ArrayList<List<String>>();
        Map<FeedItemUser, Double> tfIdfMap = new LinkedHashMap<FeedItemUser, Double>();

        // add main document (query)
        docs.add(query);

        // add documents with which the main query is to be compared.
        // These are all feed items except the 100 feed items that make up the
        // main query.
        feedsToSort.stream().forEach(e -> {
            if (e.getLastReadingDate() == null) {
                String language = e.getFeedItem().getLanguage();
                String title = e.getFeedItem().getTitle();
                String description = e.getFeedItem().getDescription();
                // a document consists of the title and the description of a feed item
                String doc = title + " " + description;
                List<String> cleanedDocument = RecommenderUtil.cleanDocument(doc, language);
                docs.add(cleanedDocument);
                // initialize the similarity of feedItemsUser to the main query with 0.
                tfIdfMap.put(e, 0.0);
            }
        });

        // similarity between main document (query) and other docs
        TF_IDF tfIdf = new TF_IDF(docs);
        for (int i = 1; i < tfIdf.getDocs().size(); i++) {
            FeedItemUser key = (FeedItemUser) tfIdfMap.keySet().toArray()[i - 1];
            tfIdfMap.put(key, tfIdf.getSimilarity(0, i));
        }

        // sort similarity in desc order
        Map<FeedItemUser, Double> reverseSortedMap = tfIdfMap.entrySet().stream()
                .sorted(Map.Entry.<FeedItemUser, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        reverseSortedMap.forEach((k, v) -> System.out.println(k.getId() + " -> " + v));

        List<FeedItemUser> feedItemsUser = reverseSortedMap.keySet().stream().collect(Collectors.toList());

        return feedItemsUser;
    }

    private List<String> createSortingByRelevanceQuery(User user) {
        // create a query of the 100 recently read feeds.
        List<FeedItemUser> top100RecentlyReadFeeds = recentlyReadFeedItemUserRepository
                .findTop100ByUserAndLastReadingDateNotNullOrderByLastReadingDateDesc(user);
        List<List<String>> bagsOfWords = top100RecentlyReadFeeds.stream().map(e -> {
            String language = e.getFeedItem().getLanguage();
            String currentQuery = e.getFeedItem().getTitle() + " " + e.getFeedItem().getDescription();
            List<String> wordList = RecommenderUtil.cleanDocument(currentQuery, language);
            return wordList;
        }).collect(Collectors.toList());

        List<String> query = new ArrayList<>();
        bagsOfWords.forEach(query::addAll);

        return query;
    }

}

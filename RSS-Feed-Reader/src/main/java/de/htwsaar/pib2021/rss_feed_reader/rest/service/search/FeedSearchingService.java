package de.htwsaar.pib2021.rss_feed_reader.rest.service.search;



import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;

@Service
public class FeedSearchingService {

    @Autowired
    private AllFeedsSearchingService allFeedsSearchingService;

    public List<FeedItemUser> searchAll(String q, User user, String period, String order, int pageNumber) {
        return allFeedsSearchingService.findFeedItemsUser(q, user, period, order, pageNumber);
    }

    public List<FeedItemUser> searchReadLater(String q, Long id, String period, String order, int pageNumber) {
        return new ArrayList<FeedItemUser>();
    }

    public List<FeedItemUser> searchLikedFeeds(String q, Long id, String period, String order, int pageNumber) {
        return new ArrayList<FeedItemUser>();
    }

    public List<FeedItemUser> searchCategory(String q, String categoryName, Long id, String period, String order,
            int pageNumber) {
        return new ArrayList<FeedItemUser>();
    }

    public List<FeedItemUser> searchChannel(String q, String channelTitle, Long id, String period, String order,
            int pageNumber) {
        return new ArrayList<FeedItemUser>();
    }
}

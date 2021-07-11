package de.htwsaar.pib2021.rss_feed_reader.rest.service.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;

@Service
public class FeedSearchingService {

    @Autowired
    private AllFeedsSearchingService allFeedsSearchingService;
    @Autowired
    private ReadLaterFeedsSearchingService readLaterFeedsSearchingService;
    @Autowired
    private LikedFeedsSearchingService likedFeedsSearchingService;
    @Autowired
    private CategoryFeedsSearchingService categoryFeedsSearchingService;
    @Autowired
    private ChannelFeedsSearchingService channelFeedsSearchingService;

    public List<FeedItemUser> searchAll(String q, User user, String period, String order, int pageNumber) {
        return allFeedsSearchingService.findFeedItemsUser(q, user, period, order, pageNumber);
    }

    public List<FeedItemUser> searchInReadLater(String q, User user, String period, String order, int pageNumber) {
        return readLaterFeedsSearchingService.findFeedItemsUser(q, user, period, order, pageNumber);
    }

    public List<FeedItemUser> searchInLikedFeeds(String q, User user, String period, String order, int pageNumber) {
        return likedFeedsSearchingService.findFeedItemsUser(q, user, period, order, pageNumber);
    }

    public List<FeedItemUser> searchInCategory(String q, User user, String categoryName, String period, String order,
            int pageNumber) {
        return categoryFeedsSearchingService.findFeedItemsUser(q, user, categoryName, period, order, pageNumber);
    }

    public List<FeedItemUser> searchInChannel(String q, String channelTitle, User user, String period, String order,
            int pageNumber) {
        return channelFeedsSearchingService.findFeedItemsUser(q, user, channelTitle, period, order, pageNumber);
    }
}

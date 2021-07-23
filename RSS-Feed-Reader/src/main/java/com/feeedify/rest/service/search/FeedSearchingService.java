package com.feeedify.rest.service.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.User;

@Service
public class FeedSearchingService {

    @Autowired
    private AllFeedsSearchingService allFeedsSearchingService;
    @Autowired
    private ReadLaterFeedsSearchingService readLaterFeedsSearchingService;
    @Autowired
    private LikedFeedsSearchingService likedFeedsSearchingService;
    @Autowired
    private RecentlyReadFeedsSearchingService recentlyReadFeedsSearchingService;
    @Autowired
    private CategoryFeedsSearchingService categoryFeedsSearchingService;
    @Autowired
    private ChannelFeedsSearchingService channelFeedsSearchingService;

    /**
     * @param q
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    public List<FeedItemUser> searchAll(String q, User user, String period, String order, int pageNumber) {
        return allFeedsSearchingService.findFeedItemsUser(q, user, period, order, pageNumber);
    }

    /**
     * @param q
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    public List<FeedItemUser> searchInReadLater(String q, User user, String period, String order, int pageNumber) {
        return readLaterFeedsSearchingService.findFeedItemsUser(q, user, period, order, pageNumber);
    }

    /**
     * @param q
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    public List<FeedItemUser> searchInLikedFeeds(String q, User user, String period, String order, int pageNumber) {
        return likedFeedsSearchingService.findFeedItemsUser(q, user, period, order, pageNumber);
    }

    public List<FeedItemUser> searchInRecentlyReadFeeds(String q, User user, String period, String order,
            int pageNumber) {
        return recentlyReadFeedsSearchingService.findFeedItemsUser(q, user, period, order, pageNumber);
    }

    /**
     * @param q
     * @param user
     * @param categoryName
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    public List<FeedItemUser> searchInCategory(String q, User user, String categoryName, String period, String order,
            int pageNumber) {
        return categoryFeedsSearchingService.findFeedItemsUser(q, user, categoryName, period, order, pageNumber);
    }

    /**
     * @param q
     * @param channelTitle
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    public List<FeedItemUser> searchInChannel(String q, String channelTitle, User user, String period, String order,
            int pageNumber) {
        return channelFeedsSearchingService.findFeedItemsUser(q, user, channelTitle, period, order, pageNumber);
    }

}

package com.feeedify.rest.service.sortingandfiltering;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.User;

@Service
public class SortingAndFilteringFeedsService {

    @Autowired
    private AllFeedsSortAndFilterService allFeedsSortAndFilterService;
    @Autowired
    private ReadLaterFeedsSortAndFilterService readLaterFeedsSortAndFilterService;
    @Autowired
    private LikedFeedsSortAndFilterService likedFeedsSortAndFilterService;
    @Autowired
    private RecentlyReadFeedsSortAndFilterService recentlyReadFeedsSortAndFilterService;
    @Autowired
    private CategoryFeedsSortAndFilterService categoryFeedsSortAndFilterService;
    @Autowired
    private ChannelFeedsSortAndFilterService channelFeedsSortAndFilterService;

    public List<FeedItemUser> findAllFeedItemsByPeriodAndOrderAndPageNumber(User user, String period, String order,
            int pageNumber) {
        return allFeedsSortAndFilterService.findFeedItemsUser(user, period, order, pageNumber);
    }

    public List<FeedItemUser> findReadLaterFeedItemsByPeriodAndOrderAndPageNumber(User user, String period,
            String order, int pageNumber) {
        return readLaterFeedsSortAndFilterService.findFeedItemsUser(user, period, order, pageNumber);
    }

    public List<FeedItemUser> findLikedFeedItemsByPeriodAndOrderAndPageNumber(User user, String period, String order,
            int pageNumber) {
        return likedFeedsSortAndFilterService.findFeedItemsUser(user, period, order, pageNumber);
    }

    public List<FeedItemUser> findRecentlyReadFeedItemsByPeriodAndOrderAndPageNumber(User user, String period,
            String order, int pageNumber) {
        return recentlyReadFeedsSortAndFilterService.findFeedItemsUser(user, period, order, pageNumber);
    }

    public List<FeedItemUser> findCategoryFeedItemsByPeriodAndOrderAndPageNumber(User user, String categoryName,
            String period, String order, int pageNumber) {
        return categoryFeedsSortAndFilterService.findFeedItemsUser(user, categoryName, period, order, pageNumber);
    }

    public List<FeedItemUser> findChannelFeedItemsByPeriodAndOrderAndPageNumber(User user, String channelTitle,
            String period, String order, int pageNumber) {
        return channelFeedsSortAndFilterService.findFeedItemsUser(user, channelTitle, period, order, pageNumber);
    }

}

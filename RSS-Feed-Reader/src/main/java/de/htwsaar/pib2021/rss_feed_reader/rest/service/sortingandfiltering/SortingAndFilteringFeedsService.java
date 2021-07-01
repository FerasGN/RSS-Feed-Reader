package de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering;

import java.util.List;

import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;

@Service
public class SortingAndFilteringFeedsService {

    private AllFeedsSortAndFilterService allFeedsSortAndFilterService;
    private ReadLaterFeedsSortAndFilterService readLaterFeedsSortAndFilterService;
    private LikedFeedsSortAndFilterService likedFeedsSortAndFilterService;
    private CategoryFeedsSortAndFilterService categoryFeedsSortAndFilterService;
    private ChannelFeedsSortAndFilterService channelFeedsSortAndFilterService;

    public SortingAndFilteringFeedsService(AllFeedsSortAndFilterService allFeedsSortAndFilterService,
            ReadLaterFeedsSortAndFilterService readLaterFeedsSortAndFilterService,
            LikedFeedsSortAndFilterService likedFeedsSortAndFilterService,
            CategoryFeedsSortAndFilterService categoryFeedsSortAndFilterService,
            ChannelFeedsSortAndFilterService channelFeedsSortAndFilterService) {
        this.allFeedsSortAndFilterService = allFeedsSortAndFilterService;
        this.readLaterFeedsSortAndFilterService = readLaterFeedsSortAndFilterService;
        this.likedFeedsSortAndFilterService = likedFeedsSortAndFilterService;
        this.categoryFeedsSortAndFilterService = categoryFeedsSortAndFilterService;
        this.channelFeedsSortAndFilterService = channelFeedsSortAndFilterService;
    }

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

    public List<FeedItemUser> findCategoryFeedItemsByPeriodAndOrderAndPageNumber(User user, String categoryName,
            String period, String order, int pageNumber) {
        return categoryFeedsSortAndFilterService.findFeedItemsUser(user, categoryName, period, order, pageNumber);
    }

    public List<FeedItemUser> findChannelFeedItemsByPeriodAndOrderAndPageNumber(User user, String channelTitle,
            String period, String order, int pageNumber) {
        return channelFeedsSortAndFilterService.findFeedItemsUser(user, channelTitle, period, order, pageNumber);
    }

}

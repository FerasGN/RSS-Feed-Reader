package de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering;

import java.util.List;

import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
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

    public List<FeedItem> findAllFeedItemsByPeriodAndOrderAndPageNumber(User user, String period, String order,
            int pageNumber) {
        return allFeedsSortAndFilterService.findFeedItems(user, period, order, pageNumber);
    }

    public List<FeedItem> findReadLaterFeedItemsByPeriodAndOrderAndPageNumber(User user, String period, String order,
            int pageNumber) {
        return readLaterFeedsSortAndFilterService.findFeedItems(user, period, order, pageNumber);
    }

    public List<FeedItem> findLikedFeedItemsByPeriodAndOrderAndPageNumber(User user, String period, String order,
            int pageNumber) {
        return likedFeedsSortAndFilterService.findFeedItems(user, period, order, pageNumber);
    }

    public List<FeedItem> findCategoryFeedItemsByPeriodAndOrderAndPageNumber(User user, String categoryName,
            String period, String order, int pageNumber) {
        return categoryFeedsSortAndFilterService.findFeedItems(user, categoryName, period, order, pageNumber);
    }

    public List<FeedItem> findChannelFeedItemsByPeriodAndOrderAndPageNumber(User user, String channelTitle,
            String period, String order, int pageNumber) {
        return channelFeedsSortAndFilterService.findFeedItems(user, channelTitle, period, order, pageNumber);
    }

}

package de.htwsaar.pib2021.rss_feed_reader.rest.service.sorting;

import java.util.List;

import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;

@Service
public class SortingFeedsService {

    private AllFeedsSortService allFeedsSortService;
    private ReadLaterFeedsSortService readLaterFeedsSortService;
    private CategoryFeedsSortService categoryFeedsSortService;

    public SortingFeedsService(AllFeedsSortService allFeedsSortService,
            ReadLaterFeedsSortService readLaterFeedsSortService, CategoryFeedsSortService categoryFeedsSortService) {
        this.allFeedsSortService = allFeedsSortService;
        this.readLaterFeedsSortService = readLaterFeedsSortService;
        this.categoryFeedsSortService = categoryFeedsSortService;
    }

    public List<FeedItem> findAllFeedItemsByPeriodAndOrderAndPageNumber(User user, String period, String order,
            int pageNumber) {
        return allFeedsSortService.findAllFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);
    }

    public List<FeedItem> findReadLaterFeedItemsByPeriodAndOrderAndPageNumber(User user, String period, String order,
            int pageNumber) {
        return readLaterFeedsSortService.findReadLaterFeedItemsByPeriodAndOrderAndPageNumber(user, period, order,
                pageNumber);
    }

    public List<FeedItem> findCategoryFeedItemsByPeriodAndOrderAndPageNumber(User user, String categoryName,
            String period, String order, int pageNumber) {
        return categoryFeedsSortService.findCategoryFeedItemsByPeriodAndOrderAndPageNumber(user, categoryName, period,
                order, pageNumber);
    }

}

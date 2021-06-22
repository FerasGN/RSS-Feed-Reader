package de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;

@Service
public class CustomPagination {

    public List<FeedItem> getNextPageOfFeedItems(Integer pageNumber, Integer pageSize,
            List<FeedItemUser> feedItemsUsers) {
        List<FeedItem> feedItems = feedItemsUsers.stream().map((FeedItemUser e) -> e.getFeedItem())
                .collect(Collectors.toList());

        double lastPage = Math.ceil(feedItems.size() / (double) pageSize);
        int nextPage = (pageNumber * pageSize) + pageSize;

        // feeds number is less than the page size
        if (pageNumber < lastPage && feedItems.size() < pageSize)
            return feedItems;
        // next feeds page index is greater than feeditems last index
        else if (pageNumber < lastPage && nextPage > feedItems.size())
            feedItems = feedItems.subList(pageNumber * pageSize, feedItems.size());
        else if (pageNumber < lastPage)
            feedItems = feedItems.subList(pageNumber * pageSize, (pageNumber * pageSize) + pageSize);

        else
            feedItems = Collections.emptyList();

        return feedItems;
    }
}

package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.*;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FeedsService {

    private ChannelUserRepository channelUserRepository;
    private FeedItemRepository feedItemRepository;
    private FeedItemUserRepository feedItemUserRepository;
    private SortingFeedsService sortingFeedsService;

    public FeedsService(ChannelUserRepository channelUserRepository, FeedItemRepository feedItemRepository,
            FeedItemUserRepository feedItemUserRepository, SortingFeedsService sortingFeedsService) {
        this.channelUserRepository = channelUserRepository;
        this.feedItemRepository = feedItemRepository;
        this.feedItemUserRepository = feedItemUserRepository;
        this.sortingFeedsService = sortingFeedsService;
    }

    // Error messages
    private static final String NO_FEED_ITEM_AVAILABLE = "No feed item found.";

    /**
     * @param user
     * @return List<FeedItem>
     */
    public List<FeedItem> findAllFeeds(User user) {
        List<FeedItemUser> list = feedItemUserRepository.findAllByUser(user);
        List<FeedItem> list2 = new ArrayList<>();

        for (FeedItemUser feedItemUser : list) {
            list2.add(feedItemUser.getFeedItem());
        }
        return list2;
    }

    /**
     * @param channel
     * @return List<FeedItem>
     */
    public List<FeedItem> showChannelFeed(Channel channel) {
        return feedItemRepository.findAllByChannel(channel);
    }

    /**
     * @return List<FeedItem>
     */
    // public List<FeedItem> showCategoryFeeds(User user, String category) {
    // List<ChannelUser> list1 =
    // channelUserRepository.findAllByUserAndCategory(user, category);
    // List<FeedItem> list2 = new ArrayList<>();

    // for (ChannelUser channelUser : list1) {
    // list2.addAll(feedItemRepository.findAllByChannel(channelUser.getChannel()));
    // }
    // return list2;
    // }

    public List<FeedItem> showReadLaterFeeds(User user) {
        List<FeedItemUser> list = feedItemUserRepository.findAllByUserAndReadLater(user, true);
        List<FeedItem> list2 = new ArrayList<>();

        for (FeedItemUser feedItemUser : list) {
            list2.add(feedItemUser.getFeedItem());
        }
        return list2;
    }

    /**
     * @param user
     * @return List<FeedItem>
     */
    public List<FeedItem> showLikedFeeds(User user) {
        List<FeedItemUser> list = feedItemUserRepository.findAllByUserAndLiked(user, true);
        List<FeedItem> list2 = new ArrayList<>();

        for (FeedItemUser feedItemUser : list) {
            list2.add(feedItemUser.getFeedItem());
        }
        return list2;
    }

    /**
     * @param user
     * @return List<FeedItem>
     */
    public List<FeedItem> showFeedsofFavoritechannels(User user) {
        List<ChannelUser> list = channelUserRepository.findAllByUserAndFavorite(user, true);
        List<FeedItem> list2 = new ArrayList<>();

        for (ChannelUser channelUser : list) {
            list2.addAll(feedItemRepository.findAllByChannel(channelUser.getChannel()));
        }
        return list2;
    }

    /**
     * @param user
     * @param feedItem
     */
    public void likeFeed(User user, FeedItem feedItem) {
        Optional<FeedItemUser> itemOptional = feedItemUserRepository.findByUserAndFeedItem(user, feedItem);
        if (itemOptional.isPresent()) {
            FeedItemUser item = itemOptional.get();
            item.setLiked(true);
            feedItemUserRepository.save(item);
        }
    }

    /**
     * @param user
     * @param feedItem
     */
    public void markFeedAsRead(User user, FeedItem feedItem) {
        Optional<FeedItemUser> itemOptional = feedItemUserRepository.findByUserAndFeedItem(user, feedItem);
        if (itemOptional.isPresent()) {
            FeedItemUser item = itemOptional.get();
            item.setRead(true);
            feedItemUserRepository.save(item);
        }
    }

    /**
     * @param user
     * @param feedItem
     */
    public void readFeedLater(User user, FeedItem feedItem) {
        Optional<FeedItemUser> itemOptional = feedItemUserRepository.findByUserAndFeedItem(user, feedItem);
        if (itemOptional.isPresent()) {
            FeedItemUser item = itemOptional.get();
            item.setReadLater(true);
            feedItemUserRepository.save(item);
        }
    }

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItem> findAllFeeds(User user, String period, String order, int pageNumber) {
        List<FeedItem> feedItems = Collections.emptyList();
        feedItems = sortingFeedsService.findFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

        return feedItems;
    }

    int i = 0;

    /**
     * @param user
     * @param categoryName
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItem> findAllFeedsByChannelCategory(User user, String categoryName, String period, String order,
            int pageNumber) {

        List<FeedItem> feedItems = sortingFeedsService.findFeedItemsByCategoryAndPeriodAndOrderAndPageNumber(user,
                categoryName, period, order, pageNumber);

        return feedItems;
    }

}

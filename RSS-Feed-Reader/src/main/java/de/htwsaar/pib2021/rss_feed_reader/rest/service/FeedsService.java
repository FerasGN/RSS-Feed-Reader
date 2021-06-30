package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.commands.FeedItemCommand;
import de.htwsaar.pib2021.rss_feed_reader.converters.FeedItemToFeedItemCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.*;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelFeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering.CategoryFeedsSortAndFilterService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering.SortingAndFilteringFeedsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FeedsService {

    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private FeedItemRepository feedItemRepository;
    @Autowired
    private FeedItemUserRepository feedItemUserRepository;
    @Autowired
    private ChannelFeedItemUserRepository channelFeedItemUserRepository;
    @Autowired
    private CategoryFeedsSortAndFilterService categoryFeedsSortAndFilterService;
    @Autowired
    private SortingAndFilteringFeedsService sortingAndFilteringFeedsService;
    @Autowired
    private ChannelService channelService;


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

    public Long findNumberOfUnreadFeeds(User user) {
        Long count = feedItemUserRepository.findAllByUserAndReadLater(user, false).stream().count();
        return count;
    }

    public Long findNumberOfUnreadFeedsOfCategory(User user, String categoryName) {
        List<FeedItemUser> feedItemsUser = categoryFeedsSortAndFilterService
                .findFeedItemUsersOrderedyCategoryNameAndPublishLocalDate(user, categoryName, null);

        Long count = feedItemsUser.stream().filter(f -> !f.isRead()).count();
        return count;
    }

    /**
     * @param user
     * @param channel
     * @return Long
     */
    public Long findNumberOfUnreadFeedsOfChannel(User user, String channelTitle) {
        Long count = channelFeedItemUserRepository.countByUserAndFeedItem_Channel_TitleAndRead(user, channelTitle,
                false);
        return count;
    }

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItem> findAllFeedItems(User user, String period, String order, int pageNumber) {
        List<FeedItem> feedItems = Collections.emptyList();
        feedItems = sortingAndFilteringFeedsService.findAllFeedItemsByPeriodAndOrderAndPageNumber(user, period, order,
                pageNumber);

        return feedItems;
    }

    public List<FeedItem> findReadLaterFeedItems(User user, String period, String order, int pageNumber) {
        List<FeedItem> feedItems = Collections.emptyList();
        feedItems = sortingAndFilteringFeedsService.findReadLaterFeedItemsByPeriodAndOrderAndPageNumber(user, period,
                order, pageNumber);

        return feedItems;
    }

    public List<FeedItem> findLikedFeedItems(User user, String period, String order, int pageNumber) {
        List<FeedItem> feedItems = Collections.emptyList();
        feedItems = sortingAndFilteringFeedsService.findLikedFeedItemsByPeriodAndOrderAndPageNumber(user, period, order,
                pageNumber);

        return feedItems;
    }

    /**
     * @param user
     * @param categoryName
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItem> findCategoryFeedItems(User user, String categoryName, String period, String order,
            int pageNumber) {

        List<FeedItem> feedItems = sortingAndFilteringFeedsService
                .findCategoryFeedItemsByPeriodAndOrderAndPageNumber(user, categoryName, period, order, pageNumber);

        return feedItems;
    }

    public List<FeedItem> findChannelFeedItems(User user, String channelUrl, String period, String order,
            int pageNumber) {

        List<FeedItem> feedItems = sortingAndFilteringFeedsService
                .findChannelFeedItemsByPeriodAndOrderAndPageNumber(user, channelUrl, period, order, pageNumber);

        return feedItems;
    }

    public List<FeedItemCommand> findAllFeedItemsCommands(User user, String period, String order, int pageNumber) {

        List<FeedItem> feedItems = findAllFeedItems(user, period, order, pageNumber);
        List<FeedItemCommand> feedItemCommands = convertFeedItemsToFeedItemCommands(user, feedItems);

        return feedItemCommands;
    }

    public List<FeedItemCommand> findReadLaterFeedItemsCommands(User user, String period, String order,
            int pageNumber) {

        List<FeedItem> feedItems = findReadLaterFeedItems(user, period, order, pageNumber);
        List<FeedItemCommand> feedItemCommands = convertFeedItemsToFeedItemCommands(user, feedItems);

        return feedItemCommands;
    }

    public List<FeedItemCommand> findLikedFeedItemsCommands(User user, String period, String order, int pageNumber) {

        List<FeedItem> feedItems = findLikedFeedItems(user, period, order, pageNumber);
        List<FeedItemCommand> feedItemCommands = convertFeedItemsToFeedItemCommands(user, feedItems);

        return feedItemCommands;
    }

    public List<FeedItemCommand> findCategoryFeedItemCommands(User user, String categoryName, String period,
            String order, int pageNumber) {

        List<FeedItem> feedItems = findCategoryFeedItems(user, categoryName, period, order, pageNumber);
        List<FeedItemCommand> feedItemCommands = convertFeedItemsToFeedItemCommands(user, feedItems);

        return feedItemCommands;
    }

    public List<FeedItemCommand> findChannelFeedItemCommands(User user, String channelUrl, String period, String order,
            int pageNumber) {

        List<FeedItem> feedItems = findChannelFeedItems(user, channelUrl, period, order, pageNumber);
        List<FeedItemCommand> feedItemCommands = convertFeedItemsToFeedItemCommands(user, feedItems);

        return feedItemCommands;
    }

    private List<FeedItemCommand> convertFeedItemsToFeedItemCommands(User user, List<FeedItem> feedItems) {
        List<FeedItemCommand> feedItemCommands = new ArrayList<>();
        feedItems.stream().forEach(f -> {
            // convert feedItem to feedItemCommand
            FeedItemToFeedItemCommand feedItemToFeedItemCommand = new FeedItemToFeedItemCommand();
            Category category = channelService.findChannelCategory(user, f.getChannel());
            feedItemToFeedItemCommand.setUser(user);
            feedItemToFeedItemCommand.setChannelCategory(category.getName());
            feedItemCommands.add(feedItemToFeedItemCommand.convert(f));
        });

        return feedItemCommands;
    }

    // public List<FeedItem> findAllFeedsByAllCategories(User user, String
    // categoryName, String period, String order,
    // int pageNumber) {

    // List<FeedItem> feedItems =
    // sortingFeedsService.findFeedItemsByCategoryAndPeriodAndOrderAndPageNumber(user,
    // categoryName, period, order, pageNumber);

    // return feedItems;
    // }

}

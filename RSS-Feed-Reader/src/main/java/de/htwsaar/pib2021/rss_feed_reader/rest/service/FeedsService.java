package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.commands.FeedItemCommand;
import de.htwsaar.pib2021.rss_feed_reader.converters.FeedItemToFeedItemCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.*;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.sorting.SortingFeedsService;

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
    private ChannelService channelService;

    public FeedsService(ChannelUserRepository channelUserRepository, FeedItemRepository feedItemRepository,
            FeedItemUserRepository feedItemUserRepository, SortingFeedsService sortingFeedsService,
            ChannelService channelService) {
        this.channelUserRepository = channelUserRepository;
        this.feedItemRepository = feedItemRepository;
        this.feedItemUserRepository = feedItemUserRepository;
        this.sortingFeedsService = sortingFeedsService;
        this.channelService = channelService;
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

    public List<FeedItemCommand> findAllFeedItemsCommands(User user, String period, String order, int pageNumber) {

        List<FeedItem> feedItems = Collections.emptyList();

        feedItems = findAllFeedItems(user, period, order, pageNumber);

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

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItem> findAllFeedItems(User user, String period, String order, int pageNumber) {
        List<FeedItem> feedItems = Collections.emptyList();
        feedItems = sortingFeedsService.findAllFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

        return feedItems;
    }

    public List<FeedItemCommand> findReadLaterFeedItemsCommands(User user, String period, String order,
            int pageNumber) {

        List<FeedItem> feedItems = Collections.emptyList();

        feedItems = findReadLaterFeedItems(user, period, order, pageNumber);

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

    public List<FeedItem> findReadLaterFeedItems(User user, String period, String order, int pageNumber) {
        List<FeedItem> feedItems = Collections.emptyList();
        feedItems = sortingFeedsService.findReadLaterFeedItemsByPeriodAndOrderAndPageNumber(user, period, order,
                pageNumber);

        return feedItems;
    }

    public List<FeedItemCommand> findCategoryFeedItemCommands(User user, String categoryName, String period,
            String order, int pageNumber) {

        List<FeedItem> feedItems = Collections.emptyList();

        feedItems = findCategoryFeedItems(user, categoryName, period, order, pageNumber);

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

        List<FeedItem> feedItems = sortingFeedsService.findCategoryFeedItemsByPeriodAndOrderAndPageNumber(user,
                categoryName, period, order, pageNumber);

        return feedItems;
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

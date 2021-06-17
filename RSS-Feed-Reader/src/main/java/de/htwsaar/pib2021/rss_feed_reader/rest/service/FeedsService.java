package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import static de.htwsaar.pib2021.rss_feed_reader.constants.Constants.*;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.*;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedsService {

    private ChannelUserRepository channelUserRepository;
    private FeedItemRepository feedItemRepository;
    private FeedItemUserRepository feedItemUserRepository;

    public FeedsService(ChannelUserRepository channelUserRepository, FeedItemRepository feedItemRepository,
            FeedItemUserRepository feedItemUserRepository) {
        this.channelUserRepository = channelUserRepository;
        this.feedItemRepository = feedItemRepository;
        this.feedItemUserRepository = feedItemUserRepository;
    }

    // Error messages
    private static final String NO_FEED_ITEM_AVAILABLE = "No feed item found.";

    public List<FeedItem> findAllFeeds(User user) {
        List<FeedItemUser> list = feedItemUserRepository.findAllByUser(user);
        List<FeedItem> list2 = new ArrayList<>();

        for (FeedItemUser feedItemUser : list) {
            list2.add(feedItemUser.getFeedItem());
        }
        return list2;
    }

    public List<FeedItem> showChannelFeed(Channel channel) {
        return feedItemRepository.findAllByChannel(channel);
    }

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

    public List<FeedItem> showLikedFeeds(User user) {
        List<FeedItemUser> list = feedItemUserRepository.findAllByUserAndLiked(user, true);
        List<FeedItem> list2 = new ArrayList<>();

        for (FeedItemUser feedItemUser : list) {
            list2.add(feedItemUser.getFeedItem());
        }
        return list2;
    }

    public List<FeedItem> showFeedsofFavoritechannels(User user) {
        List<ChannelUser> list = channelUserRepository.findAllByUserAndFavorite(user, true);
        List<FeedItem> list2 = new ArrayList<>();

        for (ChannelUser channelUser : list) {
            list2.addAll(feedItemRepository.findAllByChannel(channelUser.getChannel()));
        }
        return list2;
    }

    public void likeFeed(User user, FeedItem feedItem) {
        Optional<FeedItemUser> itemOptional = feedItemUserRepository.findByUserAndFeedItem(user, feedItem);
        if (itemOptional.isPresent()) {
            FeedItemUser item = itemOptional.get();
            item.setLiked(true);
            feedItemUserRepository.save(item);
        }
    }

    public void markFeedAsRead(User user, FeedItem feedItem) {
        Optional<FeedItemUser> itemOptional = feedItemUserRepository.findByUserAndFeedItem(user, feedItem);
        if (itemOptional.isPresent()) {
            FeedItemUser item = itemOptional.get();
            item.setRead(true);
            feedItemUserRepository.save(item);
        }
    }

    public void readFeedLater(User user, FeedItem feedItem) {
        Optional<FeedItemUser> itemOptional = feedItemUserRepository.findByUserAndFeedItem(user, feedItem);
        if (itemOptional.isPresent()) {
            FeedItemUser item = itemOptional.get();
            item.setReadLater(true);
            feedItemUserRepository.save(item);
        }
    }

    public List<FeedItem> findAllFeeds(User user, String period, String order, int pageNumber) {
        List<FeedItem> feedItems = Collections.emptyList();
        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);

        Page<FeedItemUser> page = feedItemUserRepository.findAllByUser(user, pageable);

        if (!page.isEmpty())
            feedItems = getFeedItemsPage(period, page);

        return feedItems;
    }

    private List<FeedItem> getFeedItemsPage(String period, Page<FeedItemUser> page) {
        List<FeedItem> feedItems = Collections.emptyList();
        switch (period) {
            case PERIOD_ALL:
                feedItems = page.getContent()
                                .stream()
                                .map((FeedItemUser e) -> e.getFeedItem())
                                .collect(Collectors.toList());
                break;

                case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItems = page.getContent()
                                .stream()
                                .filter(e -> {
                                            LocalDate dateOfFeedItem = e.getFeedItem()
                                                                        .getPublishDate()
                                                                        .toLocalDate();
                                            return dateOfFeedItem.isEqual(today); })
                                         .map((FeedItemUser e) -> e.getFeedItem())
                                         .collect(Collectors.toList());
                break;
                
                case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItems = page.getContent()
                                .stream()
                                .filter(e -> {
                                            LocalDate dateOfFeedItem = e.getFeedItem()
                                                                        .getPublishDate()
                                                                        .toLocalDate();
                                            return dateOfFeedItem.isEqual(dateBeforeSevenDays) 
                                            || dateOfFeedItem.isAfter(dateBeforeSevenDays);})
                                .map((FeedItemUser e) -> e.getFeedItem())
                                .collect(Collectors.toList());
                break;

                case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItems = page.getContent()
                                .stream()
                                .filter(e -> {
                                            LocalDate dateOfFeedItem = e.getFeedItem()
                                                                        .getPublishDate()
                                                                        .toLocalDate();
                                            return dateOfFeedItem.isEqual(dateBeforeThirtyDays) 
                                            || dateOfFeedItem.isAfter(dateBeforeThirtyDays);})
                                .map((FeedItemUser e) -> e.getFeedItem())
                                .collect(Collectors.toList());
                break;

            default:
                break;
        }
        return feedItems;
    }

    public List<FeedItem> findAllFeedsByChannelCategory(User user, String categoryName, String period, String order,
            int pageNumber) {
        List<FeedItem> feedItems = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNumber, 2);

        Page<ChannelUser> page = channelUserRepository.findAllByUserAndCategory_Name(user,
                categoryName.trim().toLowerCase(), pageable);

        if (!page.isEmpty()) {
            switch (period) {
                case PERIOD_ALL:
                    page.getContent()
                        .stream()
                        .forEach(channelUser -> feedItems.addAll(channelUser.getChannel().getFeedItems()));
                    break;
                
                case PERIOD_TODAY:
                break;

                case PERIOD_LAST_SEVEN_DAYS:
                break;
                
                case PERIOD_LAST_THIRTY_DAYS:
                break;

                default:
                break;
            }

        }

        return feedItems;
    }

}

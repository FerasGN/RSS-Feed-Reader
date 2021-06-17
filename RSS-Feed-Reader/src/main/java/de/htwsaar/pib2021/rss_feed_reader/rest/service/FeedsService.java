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
        feedItems = findFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

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
                    page.getContent().stream()
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

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    private List<FeedItem> findFeedItemsByPeriodAndOrderAndPageNumber(User user, String period, String order,
            int pageNumber) {

        List<FeedItem> feedItems = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                // set start date to null when all feeds are needed
                feedItems = findFeedItemsByStartDateAndOrderAndPageNumber(user, null, order, pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItems = findFeedItemsByStartDateAndOrderAndPageNumber(user, today, order, pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItems = findFeedItemsByStartDateAndOrderAndPageNumber(user, dateBeforeSevenDays, order, pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItems = findFeedItemsByStartDateAndOrderAndPageNumber(user, dateBeforeThirtyDays, order,
                        pageNumber);
                break;

            default: {
                // find all feeds with the given page number
                feedItems = findFeedItemsByStartDateAndOrderAndPageNumber(user, null, order, pageNumber);
                break;
            }
        }
        return feedItems;
    }

    /**
     * @param user
     * @param startDate
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    private List<FeedItem> findFeedItemsByStartDateAndOrderAndPageNumber(User user, LocalDate startDate, String order,
            int pageNumber) {

        List<FeedItemUser> feedItemsUsers = findFilteredAndSortedFeedItemsUsers(user, startDate, order, pageNumber);
        List<FeedItem> feedItems = feedItemsUsers.stream().map((FeedItemUser e) -> e.getFeedItem())
                .collect(Collectors.toList());
        return feedItems;
    }

    /**
     * @param user
     * @param startDate
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    private List<FeedItemUser> findFilteredAndSortedFeedItemsUsers(User user, LocalDate startDate, String order,
            int pageNumber) {

        List<FeedItemUser> feedItemsUsers = Collections.emptyList();
        Pageable pageable = null;
        Page<FeedItemUser> page = null;

        switch (order) {
            case ORDER_BY_LATEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                // no start date means we need all feeds
                if (startDate == null)
                    page = feedItemUserRepository.findByUserOrderByFeedItem_PublishDateDesc(user, pageable);
                else
                    page = feedItemUserRepository
                            .findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(user,
                                    startDate, pageable);

                feedItemsUsers = page.getContent();
                break;
            } // end case

            case ORDER_BY_OLDEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                if (startDate == null)
                    page = feedItemUserRepository.findByUserOrderByFeedItem_PublishDateAsc(user, pageable);
                else
                    page = feedItemUserRepository
                            .findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(user,
                                    startDate, pageable);

                feedItemsUsers = page.getContent();
                break;
            } // end case

            case ORDER_BY_CATEGORY: {

                break;
            } // end case

            default:

                break;
        }// end switch
        return feedItemsUsers;
    }

}

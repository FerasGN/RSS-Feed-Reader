package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.*;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.NoFeedAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class FeedsService {

    private ChannelUserRepository channelUserRepository;
    private FeedItemRepository feedItemRepository;
    private FeedItemUserRepository feedItemUserRepository;

    private static  final int PAGE_SIZE = 10;

    public FeedsService(ChannelUserRepository channelUserRepository, FeedItemRepository feedItemRepository,
            FeedItemUserRepository feedItemUserRepository) {
        this.channelUserRepository = channelUserRepository;
        this.feedItemRepository = feedItemRepository;
        this.feedItemUserRepository = feedItemUserRepository;
    }

    public FeedsService() {
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

    public List<FeedItem> showCategoryFeeds(User user, String category) {
        List<ChannelUser> list1 = channelUserRepository.findAllByUserAndCategory(user, category);
        List<FeedItem> list2 = new ArrayList<>();

        for (ChannelUser channelUser : list1) {
            list2.addAll(feedItemRepository.findAllByChannel(channelUser.getChannel()));
        }
        return list2;
    }

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
        Pageable pageable = PageRequest.of(pageNumber , PAGE_SIZE);
        Calendar calendar = Calendar.getInstance();
        Page<FeedItem> page = null;

        int day = calendar.get(Calendar.DATE);
        //Note: +1 the month for current month
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);


        switch (period){
            case "today":
                switch (order){
                    case "OrderByLatest":
                        page = feedItemRepository.findByUsersIsAndPublishDate_YearAndPublishDate_MonthAndPublishDate_DayOfMonthOrderByPublishDateAsc
                                (user, year, month, day, pageable);
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
        List<FeedItem> feedItems = page.getContent();
        return feedItems;
    }

}

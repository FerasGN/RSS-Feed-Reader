package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.*;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.NoFeedAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

@Service
public class FeedsService {

    @Autowired
    ChannelUserRepository channelUserRepository;
    @Autowired
    FeedItemRepository feedItemRepository;
    @Autowired
    FeedItemUserRepository feedItemUserRepository;
    // Error messages
    private static final String NO_FEED_ITEM_AVAILABLE = "No feed item found.";

    public List<FeedItem> showAllFeeds(User user){
        List<FeedItemUser> list = feedItemUserRepository.findAllByUser(user);
        List<FeedItem> list2 = new ArrayList<>();

        for(FeedItemUser feedItemUser: list){
            list2.add(feedItemUser.getFeedItem());
        }
        return list2;
    }

    public List<FeedItem> showChannelFeed(Channel channel){
       return feedItemRepository.findAllByChannel(channel);
    }

    public List<FeedItem> showCategoryFeeds(User user, String category){
        List<ChannelUser> list1 = channelUserRepository.findAllByUserAndCategory(user, category);
        List<FeedItem> list2 = new ArrayList<>();

        for(ChannelUser channelUser: list1){
            list2.addAll(feedItemRepository.findAllByChannel(channelUser.getChannel()));
        }
        return list2;
    }

    public List<FeedItem> showReadLaterFeeds(User user){
        List<FeedItemUser> list = feedItemUserRepository.findAllByUserAndReadLater(user, true);
        List<FeedItem> list2 = new ArrayList<>();

        for(FeedItemUser feedItemUser: list){
            list2.add(feedItemUser.getFeedItem());
        }
        return list2;
    }

    public List<FeedItem> showLikedFeeds(User user){
        List<FeedItemUser> list = feedItemUserRepository.findAllByUserAndLiked(user, true);
        List<FeedItem> list2 = new ArrayList<>();

        for(FeedItemUser feedItemUser: list){
            list2.add(feedItemUser.getFeedItem());
        }
        return list2;
    }

    public List<FeedItem> showFeedsofFavoritechannels(User user){
        List<ChannelUser> list = channelUserRepository.findAllByUserAndFavorite(user, true);
        List<FeedItem> list2 = new ArrayList<>();

        for(ChannelUser channelUser: list){
            list2.addAll(feedItemRepository.findAllByChannel(channelUser.getChannel()));
        }
        return list2;
    }

    public void likeFeed(User user, FeedItem feedItem){
        FeedItemUser item = feedItemUserRepository.findByUserAndFeedItem(user, feedItem);
        item.setLiked(true);
        feedItemUserRepository.save(item);
    }

    public void markFeedAsRead(User user, FeedItem feedItem){
        FeedItemUser item = feedItemUserRepository.findByUserAndFeedItem(user, feedItem);
        item.setRead(true);
        feedItemUserRepository.save(item);
    }

    public void readFeedLater(User user, FeedItem feedItem){
        FeedItemUser item = feedItemUserRepository.findByUserAndFeedItem(user, feedItem);
        item.setReadLater(true);
        feedItemUserRepository.save(item);
    }


    /**
     * Methods takes a function as argument. The function should be the corresponding
     * lambda expression to the various methods sortTodayFeeds, sortThisWeekFeeds,
     * sortThisMonthFeeds...
     * The Lambda expression should be passed when calling the method in the controller.
     *
     * Exemple: Predicate for sortThisWeekFeeds looks like:
     * Predicate<FeedItem> todayFeeds = feedItem -> {
     *         return (feedItem.getPublishDate().toLocalDate()).equals( LocalDate.now());
     *     };
     *
     *
     * @param user
     * @param predicate
     * @return
     * @throws NoFeedAvailableException
     */
    public List<FeedItem> sortFeeds(User user, Predicate<FeedItem> predicate) throws NoFeedAvailableException{
        //Method shows only feeds of the channel a user is interested in.

        // List of todays feeds for favorite channels
        List<FeedItem> favFeedItems = new ArrayList<>();

        // List of todays feeds for other channels
        List<FeedItem> otherFeedItems = new ArrayList<>();

        List<ChannelUser> channelUser = channelUserRepository.findAllByUser(user);
        for(ChannelUser element: channelUser){
            Channel channel = element.getChannel();
            boolean favChannel = element.isFavorite();
            List<FeedItem> feedItems = channel.getFeedItems();

            //search for todays feed item
            for(FeedItem feedItem: feedItems){
                if(predicate.test(feedItem)){
                    // Prioritize favorite channel
                    if(favChannel == true){
                        favFeedItems.add(feedItem);
                    } else {
                        otherFeedItems.add(feedItem);
                    }
                }
            }
        }
        // Join the two lists together
        favFeedItems.addAll(otherFeedItems);
        if(favFeedItems.size() != 0 ){
            return favFeedItems;
        }
        throw new NoFeedAvailableException(NO_FEED_ITEM_AVAILABLE);
    }

    public List<FeedItem> sortFeeds(User user){



        return null;
    }

}

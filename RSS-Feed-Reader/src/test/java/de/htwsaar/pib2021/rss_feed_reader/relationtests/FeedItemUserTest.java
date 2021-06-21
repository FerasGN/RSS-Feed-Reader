package de.htwsaar.pib2021.rss_feed_reader.relationtests;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.FeedsService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.sorting.SortingFeedsService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.transaction.BeforeTransaction;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class FeedItemUserTest {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private FeedItemRepository feedItemRepo;
    @Autowired
    private ChannelUserRepository channelUserRepo;
    @Autowired
    private FeedItemUserRepository feedItemUserRepo;

    @Autowired
    private FeedsService feedService;
    private static final String DESC = "A short story";
    private LocalDateTime ldt = LocalDateTime.now();

    /**@BeforeTransaction
    public void init() {
        User user = new User();
        user.setId(1l);
        user.setFirstName("jon");
        user.setLastName("snow");
        user.setUsername("jsnow");
        user.setEmail("jsnow@gmail.com");
        user.setAge(20);
        user.setPassword("123");
        userRepo.save(user);

        FeedItem feedItem = new FeedItem();
        feedItem.setId(1l);
        feedItem.setDescription(DESC);
        feedItem.setLink("https://google-news");
        feedItem.setTitle("One day in my life");
        feedItem.setContent("Here is some content");
        feedItem.setPublishDate(ldt);
        feedItemRepo.save(feedItem);

        FeedItem feedItem2 = new FeedItem();
        feedItem2.setId(2l);
        feedItem2.setDescription(DESC);
        feedItem2.setLink("https://news.de");
        feedItem2.setTitle("A horrible movie");
        feedItem2.setContent("Some content for feedItem2");
        feedItem2.setPublishDate(ldt);
        feedItemRepo.save(feedItem2);

        FeedItemUserId feedItemUserId = new FeedItemUserId(feedItem.getId(), user.getId());
        FeedItemUser feedItemUser = new FeedItemUser();
        feedItemUser.setId(feedItemUserId);
        feedItemUser.setRead(true);
        feedItemUser.setLiked(true);
        feedItemUser.setReadLater(false);
        feedItemUser.setUser(user);
        feedItemUser.setFeedItem(feedItem);
        feedItemUserRepo.save(feedItemUser);

        FeedItemUserId feedItemUserId_ = new FeedItemUserId(2l, 1l);
        FeedItemUser feedItemUser_ = new FeedItemUser();
        feedItemUser_.setId(feedItemUserId_);
        feedItemUser_.setRead(true);
        feedItemUser_.setLiked(true);
        feedItemUser_.setReadLater(false);
        feedItemUser_.setUser(user);
        feedItemUser_.setFeedItem(feedItem2);
        feedItemUserRepo.save(feedItemUser_);

    }

    @Test
    public void findUserIdInFeedItemUser() {
        FeedItemUserId feedItemUserId = new FeedItemUserId(1l, 1l);
        FeedItemUser feedItemUser1 = feedItemUserRepo.findById(feedItemUserId).get();
        User user = userRepo.findById(feedItemUser1.getUser().getId()).get();
        assertEquals(user.getLastName(), "snow");
    }

    @Test
    public void findFeedItemInFeedItemUser() {
        FeedItemUserId feedItemUserId = new FeedItemUserId(1l, 1l);
        FeedItemUser feedItemUser1 = feedItemUserRepo.findById(feedItemUserId).get();
        FeedItem feedItem = feedItemRepo.findById(feedItemUser1.getFeedItem().getId()).get();
        assertEquals(feedItem.getTitle(), "One day in my life");
    }

    @Test
    public void checkNumberOfFeedItemsOfUser() {
        List<FeedItemUser> feedItemUsers = feedItemUserRepo.findAll();
        long feedItemId = feedItemUsers.get(1).getFeedItem().getId();
        assertEquals(feedItemId, 2l);
    }
     */

    @Test
    public void checkFeed(){
        User user = userRepo.findById(1L).get();
        //List<FeedItem> feeds =  feedsService.findAllFeeds(user, "today", "OrderByLatest", 0);
        assertEquals(user.getUsername(), "rochelle");

    }
}

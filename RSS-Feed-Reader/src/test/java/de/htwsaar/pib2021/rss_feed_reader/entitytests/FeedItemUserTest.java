package de.htwsaar.pib2021.rss_feed_reader.entitytests;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional
public class FeedItemUserTest {

    @Autowired
    private FeedItemUserRepository feedItemUserRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private FeedItemRepository feedItemRepo;
    private LocalDateTime ldt = LocalDateTime.now();

    @BeforeTransaction
    public void init() {
        User user = new User();
        user.setId(1l);
        user.setFirstName("jon");
        user.setLastName("snow");
        user.setUsername("jsnow");
        user.setEmail("jsnow@gmail.com");
        user.setAge(20);
        user.setPassword("123");
        user = userRepo.save(user);

        FeedItem feedItem = new FeedItem();
        feedItem.setId(1l);
        feedItem.setDescription("A short description");
        feedItem.setLink("https://google-news");
        feedItem.setTitle("One day in my life");
        feedItem.setContent("Here is some content");
        feedItem.setPublishDate(ldt);
        feedItem = feedItemRepo.save(feedItem);

        FeedItemUser feedItemUser = new FeedItemUser();
        feedItemUser.setClicks(5);
        feedItemUser.setUser(user);
        feedItemUser.setFeedItem(feedItem);
        feedItemUserRepo.save(feedItemUser);
    }

    @Test
    public void testFindFeedItemUser() {
        FeedItemUser feedItemUser = feedItemUserRepo.findById(new FeedItemUserId(1l, 1l)).get();

        assertNotNull(feedItemUser);
    }

    @Test
    public void testFindUserInFeedItemUser() {
        FeedItemUser feedItemUser = feedItemUserRepo.findById(new FeedItemUserId(1l, 1l)).get();

        assertEquals(feedItemUser.getUser().getUsername(), "jsnow");
    }

    @Test
    public void testFindFeedItemInFeedItemUser() {
        FeedItemUser feedItemUser = feedItemUserRepo.findById(new FeedItemUserId(1l, 1l)).get();

        assertEquals(feedItemUser.getFeedItem().getTitle(), "One day in my life");
    }

}

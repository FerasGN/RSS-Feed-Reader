package de.htwsaar.pib2021.rss_feed_reader.entitytests;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
public class FeedItemUserTest {

    @Autowired
    private FeedItemUserRepository feedItemUserRepo;
    private FeedItemUserId feedItemUserId = new FeedItemUserId(1l, 1l);
    private ZonedDateTime zone= ZonedDateTime.now();
    
    @Test
    @Rollback(false)
    public void saveFeedItemUserTest(){
        FeedItem feedItem = new FeedItem();
        feedItem.setId(1l);
        feedItem.setDescription("A short description");
        feedItem.setLink("https://google-news");
        feedItem.setTitle("One day in my life");
        feedItem.setContent("Here is some content");
        feedItem.setPublishDate(zone);

        User user = new User();
        user.setId(1l);
        user.setFirstName("jon");
        user.setLastName("snow");
        user.setUsername("jsnow");
        user.setEmail("jsnow@gmail.com");
        user.setAge(20);
        user.setPassword("123");

        FeedItemUser feedItemUser = new FeedItemUser();
        feedItemUser.setClicks(5);
        feedItemUser.setUser(user);
        feedItemUser.setFeedItem(feedItem);

        feedItemUser = feedItemUserRepo.save(feedItemUser);
        assertEquals(feedItemUser.getId().getFeedItemId(), 1l);
    }

}

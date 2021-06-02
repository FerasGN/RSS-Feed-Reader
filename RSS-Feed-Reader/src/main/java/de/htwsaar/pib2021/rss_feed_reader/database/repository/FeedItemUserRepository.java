package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;

import java.util.List;

public interface FeedItemUserRepository extends JpaRepository<FeedItemUser, FeedItemUserId> {

    List<FeedItemUser> findAllByUserAndRead(User user, Boolean bool);

    List<FeedItemUser> findAllByUserAndLiked(User user, Boolean bool);

    List<FeedItemUser> findAllByUserAndReadLater(User user, Boolean bool);

    List<FeedItemUser> findAllByUser(User user);

    FeedItemUser findByUserAndFeedItem(User user, FeedItem feedItem);

}

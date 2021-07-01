package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedItemUserRepository extends JpaRepository<FeedItemUser, FeedItemUserId> {

        List<FeedItemUser> findAllByUserAndRead(User user, boolean read);

        List<FeedItemUser> findAllByUserAndLiked(User user, boolean liked);

        List<FeedItemUser> findAllByUserAndReadLater(User user, boolean readLater);

        Long countByUserAndRead(User user, boolean read);

        List<FeedItemUser> findAllByUser(User user);

        Optional<FeedItemUser> findByUserAndFeedItem(User user, FeedItem feedItem);

        Optional<FeedItemUser> findByUserAndFeedItem_Link(User user, String link);

        Page<FeedItemUser> findAllByUser(User user, Pageable pageable);

}

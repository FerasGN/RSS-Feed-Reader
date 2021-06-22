package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;

@Repository
public interface ReadLaterFeedItemUserRepository extends JpaRepository<FeedItemUser, FeedItemUserId> {

    Page<FeedItemUser> findByUserAndReadLaterOrderByFeedItem_PublishDateDesc(User user, Boolean readLater,
            Pageable pageable);

    Page<FeedItemUser> findByUserAndReadLaterOrderByFeedItem_PublishDateAsc(User user, Boolean readLater,
            Pageable pageable);

    Page<FeedItemUser> findByUserAndReadLaterAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(
            User user, Boolean readLater, LocalDate publishLocalDate, Pageable pageable);

    Page<FeedItemUser> findByUserAndReadLaterAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(
            User user, Boolean readLater, LocalDate publishLocalDate, Pageable pageable);

    Page<FeedItemUser> findByUserAndReadLaterOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(User user,
            Boolean readLater, Pageable pageable);

    Page<FeedItemUser> findByUserAndReadLaterAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(
            User user, Boolean readLater, LocalDate publishLocalDate, Pageable pageable);

    List<FeedItemUser> findByUserAndReadLaterAndFeedItem_Channel(User user, Boolean readLater, Channel channel);

    List<FeedItemUser> findByUserAndReadLaterAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(User user,
            Boolean readLater, Channel channel, LocalDate publishLocalDate);
}

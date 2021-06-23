package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AllFeedItemUserRepository extends JpaRepository<FeedItemUser, FeedItemUserId> {

    Page<FeedItemUser> findByUserOrderByFeedItem_PublishDateDesc(User user, Pageable pageable);

    Page<FeedItemUser> findByUserOrderByFeedItem_PublishDateAsc(User user, Pageable pageable);

    Page<FeedItemUser> findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(User user,
            LocalDate publishLocalDate, Pageable pageable);

    Page<FeedItemUser> findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(User user,
            LocalDate publishLocalDate, Pageable pageable);

    Page<FeedItemUser> findByUserOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(User user, Pageable pageable);

    Page<FeedItemUser> findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(
            User user, LocalDate publishLocalDate, Pageable pageable);

    List<FeedItemUser> findByUserAndFeedItem_Channel(User user, Channel channel);

    List<FeedItemUser> findByUserAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(User user,
            Channel channel, LocalDate publishLocalDate);

}
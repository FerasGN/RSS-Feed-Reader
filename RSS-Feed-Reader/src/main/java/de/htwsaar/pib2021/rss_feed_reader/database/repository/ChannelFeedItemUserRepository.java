package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;

@Repository
public interface ChannelFeedItemUserRepository extends JpaRepository<FeedItemUser, FeedItemUserId> {

    Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleOrderByFeedItem_PublishDateDesc(User user,
            String channelTitle, Pageable pageable);

    Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleOrderByFeedItem_PublishDateAsc(User user, String channelTitle,
            Pageable pageable);

    Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(
            User user, String channelTitle, LocalDate publishLocalDate, Pageable pageable);

    Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(
            User user, String channelTitle, LocalDate publishLocalDate, Pageable pageable);

    Page<FeedItemUser> findByUserAndLikedOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(User user,
            String channelTitle, Pageable pageable);

    Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(
            User user, String channelTitle, LocalDate publishLocalDate, Pageable pageable);

}
package com.feeedify.database.repository;

import com.feeedify.database.entity.Channel;
import com.feeedify.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.compositeIds.FeedItemUserId;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AllFeedItemUserRepository extends JpaRepository<FeedItemUser, FeedItemUserId> {

        List<FeedItemUser> findByUserOrderByFeedItem_PublishDateDesc(User user);

        List<FeedItemUser> findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByReadAscFeedItem_PublishDateDesc(
                        User user, LocalDate startDate);

        Page<FeedItemUser> findByUserOrderByFeedItem_PublishDateDesc(User user, Pageable pageable);

        Page<FeedItemUser> findByUserOrderByFeedItem_PublishDateAsc(User user, Pageable pageable);

        Page<FeedItemUser> findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(
                        User user, LocalDate publishLocalDate, Pageable pageable);

        Page<FeedItemUser> findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(
                        User user, LocalDate publishLocalDate, Pageable pageable);

        Page<FeedItemUser> findByUserOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(User user,
                        Pageable pageable);

        Page<FeedItemUser> findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(
                        User user, LocalDate publishLocalDate, Pageable pageable);

        List<FeedItemUser> findByUserAndFeedItem_Channel(User user, Channel channel);

        List<FeedItemUser> findByUserAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(User user,
                        Channel channel, LocalDate publishLocalDate);

        Page<FeedItemUser> findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByReadAscFeedItem_PublishDateDesc(
                        User user, LocalDate startDate, Pageable pageable);

        Page<FeedItemUser> findByUserOrderByReadAscFeedItem_PublishDateDesc(User user, Pageable pageable);

}

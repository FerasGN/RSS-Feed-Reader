package com.feeedify.database.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.User;
import com.feeedify.database.entity.compositeIds.FeedItemUserId;

@Repository
public interface ChannelFeedItemUserRepository extends JpaRepository<FeedItemUser, FeedItemUserId> {

        Long countByUserAndFeedItem_Channel_TitleAndRead(User user, String channelTitle, boolean read);

        Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleOrderByFeedItem_PublishDateDesc(User user,
                        String channelTitle, Pageable pageable);

        Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleOrderByFeedItem_PublishDateAsc(User user,
                        String channelTitle, Pageable pageable);

        Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(
                        User user, String channelTitle, LocalDate publishLocalDate, Pageable pageable);

        Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(
                        User user, String channelTitle, LocalDate publishLocalDate, Pageable pageable);

        Page<FeedItemUser> findByUserAndLikedOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(User user,
                        String channelTitle, Pageable pageable);

        Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(
                        User user, String channelTitle, LocalDate publishLocalDate, Pageable pageable);

        Optional<FeedItemUser> findByIdAndFeedItem_Channel_Title(FeedItemUserId feedItemUserId, String channelTitle);

        Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleOrderByReadAscFeedItem_PublishDateDesc(User user,
                String channelTitle, Pageable pageable);

        Page<FeedItemUser> findByUserAndFeedItem_Channel_TitleAndFeedItem_publishLocalDateGreaterThanEqualOrderByReadAscFeedItem_PublishDateDesc(
                User user, String channelTitle, LocalDate startDate, Pageable pageable);

}

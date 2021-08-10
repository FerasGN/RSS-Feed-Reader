package com.feeedify.database.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.feeedify.database.entity.Channel;
import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.User;
import com.feeedify.database.entity.compositeIds.FeedItemUserId;

@Repository
public interface LikedFeedItemUserRepository extends JpaRepository<FeedItemUser, FeedItemUserId> {

        Page<FeedItemUser> findByUserAndLikedOrderByFeedItem_PublishDateDesc(User user, Boolean liked,
                        Pageable pageable);

        Page<FeedItemUser> findByUserAndLikedOrderByFeedItem_PublishDateAsc(User user, Boolean liked,
                        Pageable pageable);

        Page<FeedItemUser> findByUserAndLikedAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(
                        User user, Boolean liked, LocalDate publishLocalDate, Pageable pageable);

        Page<FeedItemUser> findByUserAndLikedAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(
                        User user, Boolean liked, LocalDate publishLocalDate, Pageable pageable);

        Page<FeedItemUser> findByUserAndLikedOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(User user,
                        Boolean liked, Pageable pageable);

        Page<FeedItemUser> findByUserAndLikedAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(
                        User user, Boolean liked, LocalDate publishLocalDate, Pageable pageable);

        List<FeedItemUser> findByUserAndLikedAndFeedItem_Channel(User user, Boolean liked, Channel channel);

        List<FeedItemUser> findByUserAndLikedAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(User user,
                        Boolean liked, Channel channel, LocalDate publishLocalDate);

        Optional<FeedItemUser> findByIdAndLiked(FeedItemUserId feedItemUserId, boolean liked);

        Page<FeedItemUser> findByUserAndLikedOrderByReadAscFeedItem_PublishDateDesc(User user, boolean b,
                        Pageable pageable);

        Page<FeedItemUser> findByUserAndLikedAndFeedItem_publishLocalDateGreaterThanEqualOrderByReadAscFeedItem_PublishDateDesc(
                        User user, boolean b, LocalDate startDate, Pageable pageable);

        List<FeedItemUser> findByUserAndLikedOrderByFeedItem_PublishDateDesc(User user, boolean b);

        List<FeedItemUser> findByUserAndLikedAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(
                        User user, boolean b, LocalDate startDate);
}

package com.feeedify.database.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import com.feeedify.database.entity.Channel;
import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.User;
import com.feeedify.database.entity.compositeIds.FeedItemUserId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentlyReadFeedItemUserRepository extends JpaRepository<FeedItemUser, FeedItemUserId> {

    Page<FeedItemUser> findByUserAndLastReadingDateNotNullOrderByLastReadingDateDesc(User user, Pageable pageable);

    Page<FeedItemUser> findByUserAndLastReadingDateNotNullAndFeedItem_publishLocalDateGreaterThanEqualOrderByLastReadingDateDesc(
            User user, LocalDate startDate, Pageable pageable);

    Page<FeedItemUser> findByUserAndLastReadingDateNotNullOrderByLastReadingDateAsc(User user, Pageable pageable);

    Page<FeedItemUser> findByUserAndLastReadingDateNotNullAndFeedItem_publishLocalDateGreaterThanEqualOrderByLastReadingDateAsc(
            User user, LocalDate startDate, Pageable pageable);

    Page<FeedItemUser> findByUserAndLastReadingDateNotNullOrderByFeedItem_Channel_TitleAscLastReadingDateDesc(User user,
            Pageable pageable);

    Page<FeedItemUser> findByUserAndLastReadingDateNotNullAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscLastReadingDateDesc(
            User user, LocalDate startDate, Pageable pageable);

    Collection<? extends FeedItemUser> findByUserAndLastReadingDateNotNullAndFeedItem_Channel(User user,
            Channel channel);

    Collection<? extends FeedItemUser> findByUserAndLastReadingDateNotNullAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(
            User user, Channel channel, LocalDate startDate);

    Optional<FeedItemUser> findByIdAndLastReadingDateNotNull(FeedItemUserId feedItemUserId);

}

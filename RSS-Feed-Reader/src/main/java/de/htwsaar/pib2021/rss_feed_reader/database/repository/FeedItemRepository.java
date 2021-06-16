package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FeedItemRepository extends JpaRepository<FeedItem, Long> {

    List<FeedItem> findAllByChannel(Channel channel);

    Optional<FeedItem> findByLink(String link);

    Page<FeedItem> findByUsersIsAndPublishDate_YearAndPublishDate_MonthAndPublishDate_DayOfMonthOrderByPublishDateAsc(User user, int year, int month, int day, Pageable pageable);
}

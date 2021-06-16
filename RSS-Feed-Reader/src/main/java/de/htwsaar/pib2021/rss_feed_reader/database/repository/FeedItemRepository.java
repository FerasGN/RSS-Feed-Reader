package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedItemRepository extends JpaRepository<FeedItem, Long> {

    List<FeedItem> findAllByChannel(Channel channel);

    Optional<FeedItem> findByLink(String link);


}

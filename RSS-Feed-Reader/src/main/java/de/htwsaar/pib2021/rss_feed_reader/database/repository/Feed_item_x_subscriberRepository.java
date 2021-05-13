package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Feed_item_x_subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Feed_item_x_subscriberRepository extends JpaRepository<Feed_item_x_subscriber, Long> {
}

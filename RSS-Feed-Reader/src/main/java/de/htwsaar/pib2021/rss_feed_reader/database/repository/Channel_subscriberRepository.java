package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel_subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Channel_subscriberRepository extends JpaRepository<Channel_subscriber, Long> {
}

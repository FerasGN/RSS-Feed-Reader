package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {


}

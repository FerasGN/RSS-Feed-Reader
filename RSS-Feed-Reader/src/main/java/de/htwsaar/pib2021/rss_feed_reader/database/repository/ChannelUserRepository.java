package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.ChannelUserId;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, ChannelUserId> {
}

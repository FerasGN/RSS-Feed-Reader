package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, Long> {
}

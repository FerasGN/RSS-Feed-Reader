package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;

public interface FeedItemUserRepository extends JpaRepository<FeedItemUser, Long> {
}

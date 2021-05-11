package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedItemAuthorRepository extends JpaRepository<FeedItemAuthor, Long> {
}

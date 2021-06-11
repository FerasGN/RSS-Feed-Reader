package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    Optional<Category> findByName(String name);

    @Query(value = "SELECT DISTINCT UPPER(c.name) FROM channel_user cu FULL OUTER JOIN category c on cu.category_id = c.id AND cu.user_id = ?1", nativeQuery = true)
    List<String> findAllByUser(@Param("userId") Long userId);
}

package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.ChannelUserId;

import java.util.List;
import java.util.Optional;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, ChannelUserId> {

    ChannelUser findByUserAndChannel(User user, Channel channel);

    List<ChannelUser> findAllByUserAndCategory(User user ,String category);

    List<ChannelUser> findAllByUser(User user);

    List<ChannelUser> findAllByUserAndFavorite(User user, Boolean bool);

    Optional<ChannelUser> findByUserAndUrl(User user, String url);
}

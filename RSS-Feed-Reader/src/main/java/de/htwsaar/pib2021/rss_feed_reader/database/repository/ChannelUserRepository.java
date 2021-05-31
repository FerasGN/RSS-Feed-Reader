package de.htwsaar.pib2021.rss_feed_reader.database.repository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.ChannelUserId;

public interface ChannelUserRepository extends JpaRepository<ChannelUser, ChannelUserId> {

    ChannelUser findByUserAndChannel(User user, Channel channel);
}

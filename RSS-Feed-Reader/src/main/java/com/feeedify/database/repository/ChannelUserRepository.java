package com.feeedify.database.repository;

import com.feeedify.database.entity.Channel;
import com.feeedify.database.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.feeedify.database.entity.ChannelUser;
import com.feeedify.database.entity.compositeIds.ChannelUserId;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelUserRepository extends JpaRepository<ChannelUser, ChannelUserId> {

    ChannelUser findByUserAndChannel(User user, Channel channel);

    List<ChannelUser> findAllByUserOrderByCategory_Name(User user);

    List<ChannelUser> findAllByUserAndChannel_channelUrlOrderByCategory_Name(User user, String channelUrl);

    List<ChannelUser> findAllByUserAndCategory_NameOrderByCategory_Name(User user, String category);

    List<ChannelUser> findAllByUser(User user);

    Page<ChannelUser> findAllByUserAndChannel(User user, Channel channel, Pageable pageable);

    List<ChannelUser> findAllByUserAndFavorite(User user, Boolean bool);

    Optional<ChannelUser> findByUserAndChannel_channelUrl(User user, String url);

}

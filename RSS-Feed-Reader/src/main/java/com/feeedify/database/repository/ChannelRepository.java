package com.feeedify.database.repository;

import com.feeedify.database.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Optional<Channel> findByTitle(String title);

    Optional<Channel> findByChannelUrl(String url);

}

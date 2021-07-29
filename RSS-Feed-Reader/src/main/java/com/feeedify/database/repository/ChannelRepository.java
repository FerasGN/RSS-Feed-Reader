package com.feeedify.database.repository;

import com.feeedify.database.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import javax.persistence.LockModeType;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {


    Optional<Channel> findByTitle(String title);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Channel> findByChannelUrl(String url);

}

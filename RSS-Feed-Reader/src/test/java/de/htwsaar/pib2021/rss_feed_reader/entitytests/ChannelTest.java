package de.htwsaar.pib2021.rss_feed_reader.entitytests;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ChannelTest {

    @Autowired
    private ChannelRepository channelRepo;
    private static final String DESCRIPTION = "Google-feed channel description";
    private static final String URL = "https://news.google.com";
    private static final String NAME = "Google-feed";
    private static final String NAME_ = "Spiegel";


    @Test
    @Rollback(false)
    public void saveChannelTest() {
        Channel channel = new Channel();
        channel.setId(1l);
        channel.setDescription(DESCRIPTION);
        channel.setUrl(URL);
        channel.setName(NAME);

        channel = channelRepo.save(channel);
        assertEquals(channel.getId(), 1);
    }

    @Test
    public void findAllChannelTest() {
        List<Channel> channels = channelRepo.findAll();
        assertEquals(channels.size(), 1);
    }


    @Test
    public void findChannelTest() {
        Channel channel = channelRepo.findById(1l).get();
        channel = channelRepo.save(channel);
        assertEquals(channel.getId(), 1l);
    }

    @Test
    public void updateChannelTest() {
        Channel channel = channelRepo.findById(1l).get();
        channel.setName(NAME_);
        channel = channelRepo.save(channel);
        assertEquals(channel.getName(), NAME_);
    }
}

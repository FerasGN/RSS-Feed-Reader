package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelUserRepository channelUserRepository;

    /**
     *
     * @param name
     * @return
     */
    public Channel searchForChannelName(String name){
        Optional<Channel> channel = channelRepository.findByName(name);
        if(!channel.isPresent()){
            //Channel not found exception
        }
        return channel.get();
    }

    /**
     *
     * @param url
     * @return
     */
    public Channel searchForChannelUrl(String url){
        Optional<Channel> channel = channelRepository.findByUrl(url);
        if(!channel.isPresent()){
            //Channel not found exception
        }
        return channel.get();
    }

    /**
     *
     * @param url
     */
    public void addNewChannel(String url){
        Optional<Channel> channel = channelRepository.findByUrl(url);
        if(channel.isPresent()){
            //Channel already exists
        }
        Channel channel1 = new Channel();
        channel1.setUrl(url);
        //RSS reader nach dem Channel suchen lassen
    }

    /**
     *
     * @param user
     * @param channel
     * @param favorite
     */
    public void favoriteChannel(User user, Channel channel, boolean favorite){
        ChannelUser channelUser = channelUserRepository.findByUserAndChannel(user, channel);
        if(favorite == true) {
            channelUser.setFavorite(true);
        }else if(favorite == false){
            channelUser.setFavorite(false);
        }
        channelUserRepository.save(channelUser);
    }

    /**
     *
     * @param user
     * @param channel
     */
    public void addChannelToUser(User user, Channel channel){
        ChannelUser channelUsertest = channelUserRepository.findByUserAndChannel(user, channel);
        if(!channelUsertest.equals(null)){
            //User already has Channel
        }
        try {
            ChannelUser channelUser = new ChannelUser();
            channelUser.setUser(user);
            channelUser.setChannel(channel);
            channelUser.setFavorite(false);
            channelUserRepository.save(channelUser);
        }catch (Exception e){
            //unable to save
        }
    }
}

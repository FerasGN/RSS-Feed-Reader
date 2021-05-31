package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.ChannelAlreadyExistException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.ChannelNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ChannelUserRepository channelUserRepository;

    private final static String CHANNEL_URL_NOT_FOUND = "Channel with given URL could not be found: ";
    private final static String CHANNEL_NAME_NOT_FOUND = "Channel with given name could not be found: ";
    private final static String CHANNEL_URL_EXIST = "Channel with given URL already exists:  ";

    /**
     *
     * @param name
     * @return
     */
    public Channel searchForChannelName(String name) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findByName(name);
        if(!channel.isPresent()){
            throw new ChannelNotFoundException(CHANNEL_NAME_NOT_FOUND + name);
        }
        return channel.get();
    }

    /**
     *
     * @param url
     * @return
     */
    public Channel searchForChannelUrl(String url) throws ChannelNotFoundException{
        Optional<Channel> channel = channelRepository.findByUrl(url);
        if(!channel.isPresent()){
            throw new ChannelNotFoundException(CHANNEL_URL_NOT_FOUND + url);
        }
        return channel.get();
    }

    /**
     *
     * @param url
     */
    public void addNewChannel(String url) throws ChannelAlreadyExistException{
        Optional<Channel> channel = channelRepository.findByUrl(url);
        if(channel.isPresent()){
            throw new ChannelAlreadyExistException(CHANNEL_URL_EXIST);
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
        ChannelUser channelUserTest = channelUserRepository.findByUserAndChannel(user, channel);
        if(!channelUserTest.equals(null)){
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

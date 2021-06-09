package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Category;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.CategoryRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.ChannelAlreadyExistException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.ChannelNotFoundException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.NotValidURLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ChannelUserRepository channelUserRepository;

    private final static String CHANNEL_URL_NOT_FOUND = "Channel with given URL could not be found: ";
    private final static String CHANNEL_NAME_NOT_FOUND = "Channel with given name could not be found: ";
    private final static String CHANNEL_URL_EXIST = "Channel with given URL already exists:  ";
    private final static String NOT_VALID_URL = "The given URL is not valid";
    private final static String CHANNEL_ALREADY_SUBSCRIBE = "You have already subscribed to this channel";
    private final static String ERROR_SUBSCRIBING_CHANNEL = "An error occurred while subscribing to this channel.";
    private final static String FEED_PARSING_ERROR = "Problem occurred while parsing the feed.";
    private final static String IOEXCEPTION_WHILE_SUBSCRIBING = "IOException error caught while subscribing to channel";

    /**FEED_PARSING_ERROR
     *
     * @param name
     * @return
     */
    public Channel searchForChannelTitle(String name) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findByTitle(name);
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

    public List<String> findCategories(){
        List<String> categories = Arrays.asList("Sport", "Politik", "Tech");
        return categories;
    }

    public List<ChannelUser> findAllChannelUserOrderedByCategory(){
        Channel c1 = new Channel();
        c1.setTitle("Politik1");
        Channel c2 = new Channel();
        c2.setTitle("Politik2");

        Channel c3 = new Channel();
        c3.setTitle("Tech1");
        Channel c4 = new Channel();
        c4.setTitle("Tech2");

        Channel c5 = new Channel();
        c5.setTitle("Sport1");
        Channel c6 = new Channel();
        c6.setTitle("Sport2");
    
        ChannelUser cu1 = new ChannelUser();
        cu1.setChannel(c1);
        cu1.setCategory("Politik");
        ChannelUser cu2 = new ChannelUser();
        cu2.setChannel(c2);
        cu2.setCategory("Politik");

        ChannelUser cu3 = new ChannelUser();
        cu3.setChannel(c3);
        cu3.setCategory("Tech");
        ChannelUser cu4 = new ChannelUser();
        cu4.setChannel(c4);
        cu4.setCategory("Tech");

        ChannelUser cu5 = new ChannelUser();
        cu5.setChannel(c5);
        cu5.setCategory("Sport");
        ChannelUser cu6 = new ChannelUser();
        cu6.setChannel(c6);
        cu6.setCategory("Sport");

        List<ChannelUser> channels =Arrays.asList(cu1, cu2, cu3, cu4, cu5, cu6);

        return channels;
    }

    public Long findNumberOfUnreadFeedsOfChannel(User user, Channel channel){
        // ChannelUser channelUser = channelUserRepository.findByUserAndChannel(user, channel);
        // Long numberOfUnreadFeeds = channelUser.getUser()
        //                         .getFeedItemUsers()
        //                         .stream()
        //                         .filter(feedItemUser -> feedItemUser.getFeedItem().getChannel().equals(channel)
        //                                             && !feedItemUser.isRead())
        //                         .count();
        return 99l;
    }

    public Long findNumberOfUnreadFeedsOfCategory(User user, Channel channel, String category){
        // List<ChannelUser> channelUsers = channelUserRepository.findAllByUserAndCategory(user, category);
        // Long numberOfUnreadFeeds = 0l;
        // for(ChannelUser channelUser : channelUsers){
        //     numberOfUnreadFeeds +=  channelUser.getUser()
        //                             .getFeedItemUsers()
        //                             .stream()
        //                             .filter(feedItemUser -> feedItemUser.getFeedItem().getChannel().equals(channel)
        //                                                 && !feedItemUser.isRead())
        //                             .count();
        // }
        return 100l;
    }

    public boolean isRssURLCorrect(String url) {
        URL feedSource;
        try {
            feedSource = new URL(url.trim());

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));
            if (feed.getEntries() != null) {
                return true;
            }
        } catch (IllegalArgumentException | FeedException | IOException e) {
            return false;
        }
        return false;

    }

    /**
     *
     * @param url
     * @return
     */
    public boolean existsChannelURL(User user, String url) {
        // check if user is already subscribed to the channel
        Optional<ChannelUser> channelUser = channelUserRepository.findByUserAndChannel_Url(user, url.trim());
        if (channelUser.isPresent()) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param user
     * @param url
     * @param category
     * @return
     * @throws ChannelAlreadyExistException
     * @throws NotValidURLException
     * @throws IOException
     * @throws Exception
     */
    public Optional<Channel> subscribeToChannel(User user, String url, String category) throws ChannelAlreadyExistException,
            NotValidURLException, IOException, Exception {

        // check if user is already subscribed to the channel
        Optional <ChannelUser> channelUser = channelUserRepository.findByUserAndChannel_Url(user, url);
        if (channelUser.isPresent()){
            throw new ChannelAlreadyExistException(CHANNEL_ALREADY_SUBSCRIBE);
        }
        // Add Channel to user
        try {
            URL feedSource = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            Channel channel = new Channel();
            channel.setTitle(feed.getTitle());
            channel.setUrl(url);
            channel.setDescription(feed.getDescription());
            channelRepository.save(channel);

            ChannelUser channelUser1 = new ChannelUser();
            channelUser1.setChannel(channel);
            channelUser1.setUser(user);
            channelUser1.setFavorite(false);

            // Check if category is already available in database
            Optional<Category> category1 = categoryRepository.findByName(category);
            if(category1.isPresent()){
                channelUser1.setCategory(category);
            } else{
                Category newCategory = new Category();
                newCategory.setName(category);
                categoryRepository.save(newCategory);
                channelUser1.setCategory(category);
            }
            channelUser1 =  channelUserRepository.save(channelUser1);

            return Optional.of(channelUser1.getChannel());
        } catch (MalformedURLException e){
            throw new NotValidURLException(NOT_VALID_URL);
        } catch (FeedException e){
            throw new FeedException(FEED_PARSING_ERROR);
        } catch (IOException e){
            throw new IOException(IOEXCEPTION_WHILE_SUBSCRIBING);
        } catch (Exception e){
            throw new Exception(ERROR_SUBSCRIBING_CHANNEL);
        }
       
    }

}

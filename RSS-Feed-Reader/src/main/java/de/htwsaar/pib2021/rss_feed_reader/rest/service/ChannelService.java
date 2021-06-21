package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import static de.htwsaar.pib2021.rss_feed_reader.constants.SseNotificationMessages.*;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.*;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Sse.SseNotification;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.CategoryRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.ChannelAlreadyExistException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.ChannelNotFoundException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.NotValidURLException;
import imageresolver.MainImageResolver;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChannelService {

    private ChannelRepository channelRepository;
    private CategoryRepository categoryRepository;
    private FeedItemRepository feedItemRepository;
    private FeedItemUserRepository feedItemUserRepository;
    private ChannelUserRepository channelUserRepository;
    private FaviconExtractorService faviconExtractorService;

    private ApplicationEventPublisher eventPublisher;

    private final static String CHANNEL_URL_NOT_FOUND = "Channel with given URL could not be found: ";
    private final static String CHANNEL_NAME_NOT_FOUND = "Channel with given name could not be found: ";
    private final static String CHANNEL_URL_EXIST = "Channel with given URL already exists:  ";
    private final static String NOT_VALID_URL = "The given URL is not valid";
    private final static String CHANNEL_ALREADY_SUBSCRIBE = "You have already subscribed to this channel";
    private final static String ERROR_SUBSCRIBING_CHANNEL = "An error occurred while subscribing to this channel.";
    private final static String FEED_PARSING_ERROR = "Problem occurred while parsing the feed.";
    private final static String IOEXCEPTION_WHILE_SUBSCRIBING = "IOException error caught while subscribing to channel";

    public ChannelService(ChannelRepository channelRepository, CategoryRepository categoryRepository,
            FeedItemRepository feedItemRepository, FeedItemUserRepository feedItemUserRepository,
            ChannelUserRepository channelUserRepository, FaviconExtractorService faviconExtractorService,
            ApplicationEventPublisher eventPublisher) {
        this.channelRepository = channelRepository;
        this.categoryRepository = categoryRepository;
        this.feedItemRepository = feedItemRepository;
        this.feedItemUserRepository = feedItemUserRepository;
        this.channelUserRepository = channelUserRepository;
        this.faviconExtractorService = faviconExtractorService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * FEED_PARSING_ERROR
     *
     * @param name
     * @return
     */
    public Channel searchForChannelTitle(String name) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findByTitle(name);
        if (!channel.isPresent()) {
            throw new ChannelNotFoundException(CHANNEL_NAME_NOT_FOUND + name);
        }
        return channel.get();
    }

    /**
     *
     * @param url
     * @return
     */
    public Channel searchForChannelUrl(String url) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findByChannelUrl(url);
        if (!channel.isPresent()) {
            throw new ChannelNotFoundException(CHANNEL_URL_NOT_FOUND + url);
        }
        return channel.get();
    }

    /**
     *
     * @param url
     */
    public void addNewChannel(String url) throws ChannelAlreadyExistException {
        Optional<Channel> channel = channelRepository.findByChannelUrl(url);
        if (channel.isPresent()) {
            throw new ChannelAlreadyExistException(CHANNEL_URL_EXIST);
        }
        Channel channel1 = new Channel();
        channel1.setChannelUrl(url);
        // RSS reader nach dem Channel suchen lassen
    }

    /**
     *
     * @param user
     * @param channel
     * @param favorite
     */
    public void favoriteChannel(User user, Channel channel, boolean favorite) {
        ChannelUser channelUser = channelUserRepository.findByUserAndChannel(user, channel);
        if (favorite == true) {
            channelUser.setFavorite(true);
        } else if (favorite == false) {
            channelUser.setFavorite(false);
        }
        channelUserRepository.save(channelUser);
    }

    /**
     *
     * @param user
     * @param channel
     */
    public void addChannelToUser(User user, Channel channel) {
        ChannelUser channelUserTest = channelUserRepository.findByUserAndChannel(user, channel);
        if (!channelUserTest.equals(null)) {
            // User already has Channel
        }
        try {
            ChannelUser channelUser = new ChannelUser();
            channelUser.setUser(user);
            channelUser.setChannel(channel);
            channelUser.setFavorite(false);
            channelUserRepository.save(channelUser);
        } catch (Exception e) {
            // unable to save
        }
    }

    /**
     * @param user
     * @return List<String>
     */
    public List<String> findAllChannelsCategoriesByUser(User user) {
        List<String> allCategories = categoryRepository.findAllChannelsCategoriesByUser(user.getId());
        return allCategories;
    }

    /**
     * @param user
     * @param channel
     * @return Category
     */
    public Category findChannelCategory(User user, Channel channel) {
        ChannelUser channelUser = channelUserRepository.findByUserAndChannel(user, channel);
        return channelUser.getCategory();
    }

    /**
     * @return List<ChannelUser>
     */
    public List<ChannelUser> findAllChannelUserOrderedByCategory(User user) {
        List<ChannelUser> channels = channelUserRepository.findAllByUserOrderByCategory_Name(user);
        return channels;
    }

    /**
     * @param user
     * @param channel
     * @return Long
     */
    public Long findNumberOfUnreadFeedsOfChannel(User user, Channel channel) {
        // ChannelUser channelUser = channelUserRepository.findByUserAndChannel(user,
        // channel);
        // Long numberOfUnreadFeeds = channelUser.getUser()
        // .getFeedItemUsers()
        // .stream()
        // .filter(feedItemUser ->
        // feedItemUser.getFeedItem().getChannel().equals(channel)
        // && !feedItemUser.isRead())
        // .count();
        return 99l;
    }

    /**
     * @param user
     * @param channel
     * @param category
     * @return Long
     */
    public Long findNumberOfUnreadFeedsOfCategory(User user, Channel channel, String category) {
        // List<ChannelUser> channelUsers =
        // channelUserRepository.findAllByUserAndCategory(user, category);
        // Long numberOfUnreadFeeds = 0l;
        // for(ChannelUser channelUser : channelUsers){
        // numberOfUnreadFeeds += channelUser.getUser()
        // .getFeedItemUsers()
        // .stream()
        // .filter(feedItemUser ->
        // feedItemUser.getFeedItem().getChannel().equals(channel)
        // && !feedItemUser.isRead())
        // .count();
        // }
        return 100l;
    }

    /**
     * @param url
     * @return Optional<SyndFeed>
     */
    public Optional<SyndFeed> parseRssFeedFromURL(String url) {
        URL feedSource;
        try {
            feedSource = new URL(url.trim());

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));
            if (feed.getEntries() != null) {
                return Optional.of(feed);
            }
        } catch (IllegalArgumentException | FeedException | IOException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    /**
     *
     * @param url
     * @return
     */
    public boolean existsChannelURL(User user, String url) {
        // check if user is already subscribed to the channel
        Optional<ChannelUser> channelUser = channelUserRepository.findByUserAndChannel_channelUrl(user, url.trim());
        if (channelUser.isPresent()) {
            return true;
        }
        return false;
    }

    /**
     * @param user
     * @param feed
     * @param url
     * @return Optional<Channel>
     * @throws IOException
     * @throws FeedException
     * @throws IllegalArgumentException
     */
    @Transactional
    private Optional<Channel> createAChannel(User user, SyndFeed feed, String url)
            throws IllegalArgumentException, FeedException, IOException {

        Optional<Channel> channelOptional = channelRepository.findByChannelUrl(url);
        Channel channel = null;
        if (channelOptional.isPresent()) {
            channel = channelOptional.get();
        } else {
            channel = new Channel();
            channel.setTitle(feed.getTitle());
            channel.setChannelUrl(url);
            channel.setWebsiteLink(feed.getLink());
            channel.setDescription(feed.getDescription());
            channel.setLanguage(feed.getLanguage());

            try {
                List<URL> links = faviconExtractorService.findFaviconLinks(feed.getLink());
                if (!links.isEmpty())
                    channel.setFaviconLink(links.get(0).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            channel = channelRepository.save(channel);

            // extract feed items
            saveFeedItems(user, channel, feed.getEntries());
        }

        return Optional.of(channel);
    }

    /**
     *
     * @param user
     * @param url
     * @param categoryName
     * @return
     * @throws ChannelAlreadyExistException
     * @throws NotValidURLException
     * @throws IOException
     * @throws Exception
     */
    public Optional<Channel> subscribeToChannel(User user, String url, String categoryName)
            throws ChannelAlreadyExistException, NotValidURLException, IOException, Exception {

        // Add Channel to user
        try {
            URL feedSource = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            Optional<Channel> channel = createAChannel(user, feed, url);
            ChannelUser channelUser = new ChannelUser();
            channelUser.setChannel(channel.get());
            channelUser.setUser(user);
            channelUser.setFavorite(false);

            // Check if category is already available in database
            Optional<Category> category = categoryRepository.findByName(categoryName.trim().toLowerCase());
            if (category.isPresent()) {
                channelUser.setCategory(category.get());
            } else {
                Category newCategory = new Category();
                newCategory.setName(categoryName.trim().toLowerCase());
                categoryRepository.save(newCategory);
                channelUser.setCategory(newCategory);
            }

            channelUser = channelUserRepository.save(channelUser);

            return Optional.of(channelUser.getChannel());

        } catch (MalformedURLException e) {
            throw new NotValidURLException(NOT_VALID_URL);
        } catch (FeedException e) {
            throw new FeedException(FEED_PARSING_ERROR);
        } catch (IOException e) {
            throw new IOException(IOEXCEPTION_WHILE_SUBSCRIBING);
        } catch (Exception e) {
            throw new Exception(ERROR_SUBSCRIBING_CHANNEL);
        }

    }

    /**
     * @param channelUser
     * @throws IllegalArgumentException
     * @throws FeedException
     * @throws IOException
     */
    public boolean saveFeedItems(User user, Channel channel)
            throws IllegalArgumentException, FeedException, IOException {
        URL feedSource = new URL(channel.getChannelUrl());
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));

        // Extract feedItems
        List<SyndEntry> syndFeedItems = feed.getEntries();
        return saveFeedItems(user, channel, syndFeedItems);
    }

    public boolean saveFeedItems(User user, Channel channel, List<SyndEntry> syndFeedItems)
            throws IllegalArgumentException, FeedException, IOException {
        boolean saved = false;
        // Save feedItems
        for (SyndEntry syndEntry : syndFeedItems) {
            String feedItemLink = syndEntry.getLink();
            Optional<FeedItemUser> feedItemUserOptional = feedItemUserRepository.findByUserAndFeedItem_Link(user,
                    feedItemLink);
            if (!feedItemUserOptional.isPresent()) {

                Optional<FeedItem> feedItemOptional = feedItemRepository.findByLink(feedItemLink);
                // if feed Item exists add it to the user
                if (feedItemOptional.isPresent()) {
                    FeedItemUser feedItemUser = new FeedItemUser();
                    feedItemUser.setUser(user);
                    feedItemUser.setFeedItem(feedItemOptional.get());
                    feedItemUserRepository.save(feedItemUser);
                } else {
                    // if feed item doesn't exit create new one and add it to the user
                    createFeedItem(user, channel, syndEntry);
                }
                saved = true;
            }

        }

        return saved;
    }

    /**
     * @param user
     * @param channel
     * @param syndEntry
     */
    private FeedItem createFeedItem(User user, Channel channel, SyndEntry syndEntry) {

        FeedItem feedItem = new FeedItem();
        feedItem.setChannel(channel);
        feedItem.setTitle(syndEntry.getTitle());
        feedItem.setLanguage(channel.getLanguage());
        if (syndEntry.getDescription() != null)
            feedItem.setDescription(syndEntry.getDescription().getValue());
        feedItem.setAuthor(syndEntry.getAuthor());
        feedItem.setLink(syndEntry.getLink());
        if (syndEntry.getPublishedDate() != null) {
            LocalDateTime publishDate = syndEntry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            feedItem.setPublishDate(publishDate);
            feedItem.setPublishLocalDate(publishDate.toLocalDate());
        }

        // set categories of feed item
        // If category is not yet available, create one and link the feed item to it.
        List<SyndCategory> SyndCategories = syndEntry.getCategories();
        List<Category> categories = new ArrayList<>();
        for (SyndCategory category : SyndCategories) {
            Optional<Category> checkCategory = categoryRepository.findByName(category.getName());
            if (checkCategory.isPresent()) {
                categories.add(checkCategory.get());
            } else {
                Category newCategory = new Category();
                newCategory.setName(category.getName());
                categories.add(newCategory);
            }
        }

        feedItem.setCategories(categories);

        channel.addFeedItem(feedItem);
        feedItem = feedItemRepository.save(feedItem);

        if (!categories.isEmpty())
            categoryRepository.saveAll(categories);

        // extract the main image of a feed item
        Optional<String> mainImage = MainImageResolver.resolveMainImage(syndEntry.getLink());
        if (mainImage.isPresent())
            feedItem.setImageUrl(mainImage.get());

        FeedItemUser feedItemUser = new FeedItemUser();
        feedItemUser.setUser(user);
        feedItemUser.setFeedItem(feedItem);
        feedItemUserRepository.save(feedItemUser);

        return feedItem;
    }

    /**
     * Reload channel content every hour.
     */
    @Scheduled(fixedRateString = "PT1H")
    @Transactional
    public void reloadChannel() {
        channelUserRepository.findAll().stream().forEach(cu -> {
            try {
                User user = cu.getUser();
                boolean saved = saveFeedItems(user, cu.getChannel());
                // notify user that a new feed has been added
                if (saved)
                    this.eventPublisher.publishEvent(new SseNotification(user.getUsername(), NEW_FEED_MESSAGE));

            } catch (UnknownHostException e) {
                System.out.println("Your Internet connection may have been interrupted");
            } catch (IllegalArgumentException | FeedException | IOException e) {
                e.printStackTrace();
            }
        });
    }

}

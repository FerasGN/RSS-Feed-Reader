package com.feeedify.rest.service;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import static com.feeedify.constants.SseNotificationMessages.*;

import com.feeedify.database.MaterializedViewManager;
import com.feeedify.database.entity.*;
import com.feeedify.database.entity.Sse.SseNotification;
import com.feeedify.database.entity.compositeIds.FeedItemUserId;
import com.feeedify.database.repository.CategoryRepository;
import com.feeedify.database.repository.ChannelFeedItemUserRepository;
import com.feeedify.database.repository.ChannelRepository;
import com.feeedify.database.repository.ChannelUserRepository;
import com.feeedify.database.repository.FeedItemRepository;
import com.feeedify.database.repository.FeedItemUserRepository;
import com.feeedify.database.repository.UserRepository;
import com.feeedify.exceptions.ChannelAlreadyExistException;
import com.feeedify.exceptions.NotValidURLException;
import imageresolver.MainImageResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FeedItemRepository feedItemRepository;
    @Autowired
    private FeedItemUserRepository feedItemUserRepository;
    @Autowired
    private ChannelFeedItemUserRepository channelFeedItemUserRepository;
    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private FaviconExtractorService faviconExtractorService;
    @Autowired
    MaterializedViewManager materializedViewManager;
    @Autowired
    private LanguageIdentifierService languageIdentifierService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String CHANNEL_URL_NOT_FOUND = "Channel with given URL could not be found: ";
    private final static String CHANNEL_NAME_NOT_FOUND = "Channel with given name could not be found: ";
    private final static String CHANNEL_URL_EXIST = "Channel with given URL already exists:  ";
    private final static String NOT_VALID_URL = "The given URL is not valid";
    private final static String CHANNEL_ALREADY_SUBSCRIBE = "You have already subscribed to this channel";
    private final static String ERROR_SUBSCRIBING_CHANNEL = "An error occurred while subscribing to this channel.";
    private final static String FEED_PARSING_ERROR = "Problem occurred while parsing the feed.";
    private final static String IOEXCEPTION_WHILE_SUBSCRIBING = "IOException error caught while subscribing to channel";

    /**
     * Finds the names of all channel categories which belong to to the given user.
     * 
     * TODO: delete a category if it was not used anywhere.
     * 
     * @param user
     * @return List<String> names of categories
     */
    public List<String> findAllChannelsCategoriesByUser(User user) {
        List<String> allCategories = categoryRepository.findAllChannelsCategoriesByUser(user.getId());
        return allCategories;
    }

    public void changeCategoryName(User user, String oldName, String newName) {
        Optional<Category> optionalFirstCategory = categoryRepository.findByName(oldName.toLowerCase());
        Optional<Category> optionalSecondCategory = categoryRepository.findByName(newName.toLowerCase());

        if (optionalFirstCategory.isPresent()) {
            Category category = null;

            if (optionalSecondCategory.isPresent())
                category = optionalSecondCategory.get();
            else
                category = new Category(newName.toLowerCase());

            List<ChannelUser> channelsWithCategory = channelUserRepository.findAllByUserAndCategory_Name(user,
                    oldName.toLowerCase());

            for (ChannelUser cu : channelsWithCategory) {
                cu.addCategory(category);
                categoryRepository.save(category);
            }

        }

    }

    public void deleteCategory(User user, String categoryName) {
        Optional<Category> optionalCategory = categoryRepository.findByName(categoryName.toLowerCase());
        if (optionalCategory.isPresent()) {
            List<ChannelUser> channelsWithCategory = channelUserRepository.findAllByUserAndCategory_Name(user,
                    categoryName.toLowerCase());

            for (ChannelUser cu : channelsWithCategory) {
                List<FeedItemUser> feedItemsUser = channelFeedItemUserRepository
                        .findByUserAndFeedItem_Channel_Title(user, cu.getChannel().getTitle());

                for (FeedItemUser fu : feedItemsUser)
                    feedItemUserRepository.delete(fu);

                channelUserRepository.delete(cu);
            }

        }
    }

    /**
     * Finds channel category for the given user and channel.
     * 
     * @param user
     * @param channel
     * @return Category
     */
    public Category findChannelCategory(User user, Channel channel) {
        if (channel != null) {
            ChannelUser channelUser = channelUserRepository.findByUserAndChannel(user, channel);
            return channelUser.getCategory();
        }
        return new Category();
    }

    /**
     * Finds all channels belonging to a user ordered by their category names.
     * 
     * @param user
     * @return List<ChannelUser>
     */
    public List<ChannelUser> findAllChannelUserOrderedByCategory(User user) {
        List<ChannelUser> channels = channelUserRepository.findAllByUserOrderByCategory_Name(user);
        return channels;
    }

    public List<ChannelUser> findAllChannelUser(User user) {
        List<ChannelUser> channels = channelUserRepository.findAllByUser(user);
        return channels;
    }

    public void deleteAllChannelUserByUser(User user) {
        List<ChannelUser> channelsUser = channelUserRepository.findAllByUser(user);
        channelUserRepository.deleteAll(channelsUser);
    }

    /**
     * 
     * Finds the RSS channel with the given url, saves the channel and adds it to
     * the given user.
     * 
     * 
     * @param user
     * @param url
     * @param categoryName
     * @return Optional<Channel>
     * @throws ChannelAlreadyExistException
     * @throws NotValidURLException
     * @throws IOException
     * @throws Exception
     */
    @Transactional
    public Optional<Channel> subscribeToChannel(User user, String url, String categoryName)
            throws ChannelAlreadyExistException, NotValidURLException, IOException, Exception {
        try {
            URL feedSource = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            // Create new channel or use the existing one.
            Optional<Channel> channelOptional = channelRepository.findByChannelUrl(url);
            Channel channel = null;
            if (channelOptional.isPresent())
                channel = channelOptional.get();
            else
                channel = createChannel(feed, url);

            // extract feed items of channel
            saveFeedItems(user, channel, feed.getEntries());

            // set the language of the channel
            channel = channelRepository.findById(channel.getId()).get();
            String languageOfChannel = detectLanguageOfChannel(channel);
            channel.setLanguage(languageOfChannel.toLowerCase());
            channel = channelRepository.save(channel);

            ChannelUser channelUser = createChannelUser(user, channel, categoryName);

            materializedViewManager.refreshChannleView();
            materializedViewManager.refreshFeedItem();

            return Optional.of(channelUser.getChannel());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new NotValidURLException(NOT_VALID_URL);
        } catch (FeedException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new FeedException(FEED_PARSING_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new IOException(IOEXCEPTION_WHILE_SUBSCRIBING);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new Exception(ERROR_SUBSCRIBING_CHANNEL);
        }

    }

    /**
     * 
     * 
     * 
     * 
     * @param user
     * @param feed
     * @param url
     * @return Channel
     * @throws FeedException
     * @throws IOException
     */
    private Channel createChannel(SyndFeed feed, String url) throws FeedException, IOException {
        Channel channel = new Channel();
        log.debug("Start creating a channel.. -> ");
        if (feed.getTitle() != null)
            channel.setTitle(feed.getTitle());
        else
            channel.setTitle("No channel title");
        channel.setChannelUrl(url);
        channel.setWebsiteLink(feed.getLink());
        channel.setDescription(feed.getDescription());

        try {
            List<URL> links = faviconExtractorService.findFaviconLinks(feed.getLink());
            if (!links.isEmpty())
                channel.setFaviconLink(links.get(0).toString());
        } catch (IOException e) {
            log.error("Cannot extract favicon of the channel: " + url);
        }

        channel = channelRepository.save(channel);

        return channel;
    }

    /**
     * Creates a unique relationship between the channel and the given user.
     * 
     * @param user
     * @param channel
     * @param categoryName
     * @return ChannelUser
     */
    private ChannelUser createChannelUser(User user, Channel channel, String categoryName) {
        ChannelUser channelUser = new ChannelUser();
        channelUser.setChannel(channel);
        channelUser.setUser(user);
        channelUser.setFavorite(false);
        channelUser = channelUserRepository.save(channelUser);
        channelUser = setCategory(categoryName, channelUser);

        return channelUser;
    }

    /**
     * Adds a category with the given category name to a channel that belongs to a
     * specific user.
     * 
     * @param categoryName
     * @param channelUser
     * @return ChannelUser channel of a user with a category
     */
    private ChannelUser setCategory(String categoryName, ChannelUser channelUser) {
        // Check if category is already available in database
        Optional<Category> category = categoryRepository.findByName(categoryName.trim().toLowerCase());
        if (category.isPresent()) {
            category.get().addChannelUser(channelUser);
            categoryRepository.save(category.get());
        } else {
            Category newCategory = new Category();
            newCategory.setName(categoryName.trim().toLowerCase());
            newCategory.addChannelUser(channelUser);
            categoryRepository.save(newCategory);
        }
        return channelUser;
    }

    /**
     * Checks if the given url is a valid Rss feed url.
     * 
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
            log.error(CHANNEL_URL_NOT_FOUND + url);
            return Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * Checks if the user has subscribed to the RSS feed with the given URL.
     * 
     * @param user
     * @param url
     * @return boolean true, whether the channel was found by the user
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
     * Detects the language of the given channel
     * 
     * @param channel
     * @return String
     */
    private String detectLanguageOfChannel(Channel channel) {
        List<FeedItem> feedItems = channel.getFeedItems();

        String language = "simple";
        if (feedItems != null && !feedItems.isEmpty())
            language = languageIdentifierService.searchLanguage(feedItems.get(0).getTitle());

        return language;
    }

    /**
     * 
     * 
     * @param user
     * @param channel
     * @return boolean true, whether the extracting of feed items was successful
     * @throws IllegalArgumentException
     * @throws FeedException
     * @throws IOException
     */
    public boolean loadFeedItems(User user, Channel channel)
            throws IllegalArgumentException, FeedException, IOException {
        Optional<SyndFeed> optionalFeed = parseRssFeedFromURL(channel.getChannelUrl());

        // Extract feedItems
        if (optionalFeed.isPresent()) {
            List<SyndEntry> syndFeedItems = optionalFeed.get().getEntries();
            return saveFeedItems(user, channel, syndFeedItems);
        }

        return false;
    }

    /**
     * 
     * Extracts all feed items from the given syndFeedItems.
     * 
     * @param user
     * @param channel
     * @param syndFeedItems
     * @return boolean
     * @throws IllegalArgumentException
     * @throws FeedException
     * @throws IOException
     */
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
                FeedItem feedItem = null;

                if (feedItemOptional.isPresent()) {
                    feedItem = feedItemOptional.get();
                } else {
                    feedItem = createFeedItem(user, channel, syndEntry);
                }

                createFeedItemUser(user, feedItem);

                saved = true;
            }
        }

        return saved;
    }

    /**
     * 
     * 
     * @param user
     * @param channel
     * @param syndEntry
     * @return FeedItem
     */
    private FeedItem createFeedItem(User user, Channel channel, SyndEntry syndEntry) {
        FeedItem feedItem = new FeedItem();
        // set channel
        feedItem.setChannel(channel);
        // set title
        if (syndEntry.getTitle() != null)
            feedItem.setTitle(syndEntry.getTitle());
        else
            feedItem.setTitle("No title");
        // set description
        if (syndEntry.getDescription() != null)
            feedItem.setDescription(syndEntry.getDescription().getValue());
        // set language
        String language = languageIdentifierService.searchLanguage(feedItem.getTitle());
        feedItem.setLanguage(language);
        // set author
        feedItem.setAuthor(syndEntry.getAuthor());
        // set link
        feedItem.setLink(syndEntry.getLink());
        // set PublishedDate and PublishLocalDate
        if (syndEntry.getPublishedDate() != null) {
            LocalDateTime publishDate = syndEntry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            feedItem.setPublishDate(publishDate);
            feedItem.setPublishLocalDate(publishDate.toLocalDate());
        }

        // extract the main image of a feed item
        Optional<String> mainImage = MainImageResolver.resolveMainImage(syndEntry.getLink());
        if (mainImage.isPresent())
            feedItem.setImageUrl(mainImage.get());

        // If category is not yet available, create one and link the feed item to it.
        List<SyndCategory> SyndCategories = syndEntry.getCategories();
        List<Category> categories = new ArrayList<>();
        for (SyndCategory category : SyndCategories) {
            Optional<Category> checkCategory = categoryRepository.findByName(category.getName().toLowerCase());
            if (checkCategory.isPresent()) {
                categories.add(checkCategory.get());
            } else {
                Category newCategory = new Category();
                newCategory.setName(category.getName().toLowerCase());
                categories.add(newCategory);
            }
        }
        // set channel of feed item
        channel.addFeedItem(feedItem);

        feedItem = feedItemRepository.save(feedItem);

        // set categories of feed item
        feedItem.setCategories(categories);
        if (!categories.isEmpty())
            categoryRepository.saveAll(categories);

        return feedItem;
    }

    /**
     * Creates a unique relationship between the feeditem and the given user.
     * 
     * @param user
     * @param feedItem
     */
    private void createFeedItemUser(User user, FeedItem feedItem) {
        FeedItemUser feedItemUser = new FeedItemUser();
        feedItemUser.setUser(user);
        feedItemUser.setFeedItem(feedItem);
        feedItemUserRepository.save(feedItemUser);
    }

    /**
     * Reload channels content every hour.
     */
    @Scheduled(fixedRateString = "PT1H")
    @Transactional
    public void reloadChannels() {
        channelUserRepository.findAll().stream().forEach(cu -> {
            try {
                User user = cu.getUser();
                boolean loaded = loadFeedItems(user, cu.getChannel());
                // notify user that a new feed has been added
                if (loaded) {
                    materializedViewManager.refreshFeedItem();
                    this.eventPublisher.publishEvent(new SseNotification(user.getUsername(), NEW_FEED_MESSAGE));
                }
            } catch (UnknownHostException e) {
                log.error("Your Internet connection may have been interrupted");
            } catch (IllegalArgumentException | FeedException | IOException e) {
                log.error("Cannot reload channel: " + e.getMessage());
            }
        });
    }

}

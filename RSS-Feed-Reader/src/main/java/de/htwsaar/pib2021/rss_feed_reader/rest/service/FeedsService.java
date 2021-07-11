package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.commands.FeedItemUserCommand;
import de.htwsaar.pib2021.rss_feed_reader.converters.FeedItemUserToFeedItemUserCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.*;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelFeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.search.FeedSearchingService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering.CategoryFeedsSortAndFilterService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering.SortingAndFilteringFeedsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FeedsService {

    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private FeedItemRepository feedItemRepository;
    @Autowired
    private FeedItemUserRepository feedItemUserRepository;
    @Autowired
    private ChannelFeedItemUserRepository channelFeedItemUserRepository;
    @Autowired
    private CategoryFeedsSortAndFilterService categoryFeedsSortAndFilterService;
    @Autowired
    private SortingAndFilteringFeedsService sortingAndFilteringFeedsService;
    @Autowired
    private FeedSearchingService searchingService;
    @Autowired
    private ChannelService channelService;

    /**
     * @param user
     * @return List<FeedItem>
     */
    public List<FeedItem> showFeedsofFavoritechannels(User user) {
        List<ChannelUser> list = channelUserRepository.findAllByUserAndFavorite(user, true);
        List<FeedItem> list2 = new ArrayList<>();

        for (ChannelUser channelUser : list) {
            list2.addAll(feedItemRepository.findAllByChannel(channelUser.getChannel()));
        }
        return list2;
    }

    /**
     * @param user
     * @param feedItem
     */
    public void changeLikeStatus(Long userId, Long feedItemId, boolean liked) {
        Optional<FeedItemUser> optionalFeedItemUser = feedItemUserRepository
                .findById(new FeedItemUserId(feedItemId, userId));
        if (optionalFeedItemUser.isPresent()) {
            FeedItemUser item = optionalFeedItemUser.get();
            item.setLiked(liked);
            feedItemUserRepository.save(item);
        }
    }

    /**
     * @param user
     * @param feedItem
     */
    public void changeReadStatus(Long userId, Long feedItemId, boolean read) {
        Optional<FeedItemUser> optionalFeedItemUser = feedItemUserRepository
                .findById(new FeedItemUserId(feedItemId, userId));
        if (optionalFeedItemUser.isPresent()) {
            FeedItemUser item = optionalFeedItemUser.get();
            item.setRead(read);
            feedItemUserRepository.save(item);
        }
    }

    public void incrementClicksNumber(Long userId, Long feedItemId) {
        Optional<FeedItemUser> optionalFeedItemUser = feedItemUserRepository
                .findById(new FeedItemUserId(feedItemId, userId));
        if (optionalFeedItemUser.isPresent()) {
            FeedItemUser item = optionalFeedItemUser.get();
            Integer clicksNumber = item.getClicks();
            if (clicksNumber == null)
                clicksNumber = 0;
            else
                clicksNumber = clicksNumber + 1;
            item.setClicks(clicksNumber);
            feedItemUserRepository.save(item);
        }
    }

    /**
     * @param user
     * @param feedItem
     */
    public void changeReadLaterStatus(Long userId, Long feedItemId, boolean readLater) {
        Optional<FeedItemUser> optionalFeedItemUser = feedItemUserRepository
                .findById(new FeedItemUserId(feedItemId, userId));
        if (optionalFeedItemUser.isPresent()) {
            FeedItemUser item = optionalFeedItemUser.get();
            item.setReadLater(readLater);
            feedItemUserRepository.save(item);
        }
    }

    public Long findNumberOfUnreadFeeds(User user) {
        Long count = feedItemUserRepository.countByUserAndRead(user, false);
        return count;
    }

    public Long findNumberOfUnreadFeedsOfCategory(User user, String categoryName) {
        List<FeedItemUser> feedItemsUser = categoryFeedsSortAndFilterService
                .findFeedItemUsersByCategoryNameOrderedyCategoryNameAndPublishLocalDate(user, categoryName, null);

        Long count = feedItemsUser.stream().filter(f -> !f.isRead()).count();
        return count;
    }

    /**
     * @param user
     * @param channel
     * @return Long
     */
    public Long findNumberOfUnreadFeedsOfChannel(User user, String channelTitle) {
        Long count = channelFeedItemUserRepository.countByUserAndFeedItem_Channel_TitleAndRead(user, channelTitle,
                false);
        return count;
    }

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItemUser> findAllFeedItemsUser(User user, String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findAllFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

        return feedItemsUser;
    }

    public List<FeedItemUser> findReadLaterFeedItemsUser(User user, String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findReadLaterFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

        return feedItemsUser;
    }

    public List<FeedItemUser> findLikedFeedItemsUser(User user, String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findLikedFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

        return feedItemsUser;
    }

    /**
     * @param user
     * @param categoryName
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItemUser> findCategoryFeedItemsUser(User user, String categoryName, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findCategoryFeedItemsByPeriodAndOrderAndPageNumber(user, categoryName, period, order, pageNumber);

        return feedItemsUser;
    }

    public List<FeedItemUser> findChannelFeedItemsUser(User user, String channelUrl, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findChannelFeedItemsByPeriodAndOrderAndPageNumber(user, channelUrl, period, order, pageNumber);

        return feedItemsUser;
    }

    public List<FeedItemUserCommand> findAllFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {

        List<FeedItemUser> feedItemsUser = findAllFeedItemsUser(user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> findReadLaterFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {

        List<FeedItemUser> feedItemsUser = findReadLaterFeedItemsUser(user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> findLikedFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {

        List<FeedItemUser> feedItemsUser = findLikedFeedItemsUser(user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> findCategoryFeedItemUserCommands(User user, String categoryName, String period,
            String order, int pageNumber) {

        List<FeedItemUser> feedItemsUser = findCategoryFeedItemsUser(user, categoryName, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> findChannelFeedItemUserCommands(User user, String channelUrl, String period,
            String order, int pageNumber) {

        List<FeedItemUser> feedItemsUser = findChannelFeedItemsUser(user, channelUrl, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    private List<FeedItemUserCommand> convertFeedItemsUserToFeedItemUserCommands(User user,
            List<FeedItemUser> feedItemsUser) {
        List<FeedItemUserCommand> feedItemUserCommands = new ArrayList<>();
        feedItemsUser.stream().forEach(fu -> {
            // convert feedItem to feedItemCommand
            FeedItemUserToFeedItemUserCommand feedItemUserToFeedItemUserCommand = new FeedItemUserToFeedItemUserCommand();
            Category category = channelService.findChannelCategory(user, fu.getFeedItem().getChannel());
            feedItemUserToFeedItemUserCommand.setUser(user);
            feedItemUserToFeedItemUserCommand.setChannelCategory(category.getName());
            feedItemUserCommands.add(feedItemUserToFeedItemUserCommand.convert(fu));
        });

        return feedItemUserCommands;
    }

    public List<FeedItemUserCommand> searchAllFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchAll(q, user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> searchReadLaterFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchInReadLater(q, user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> searchLikedFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchInLikedFeeds(q, user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> searchCategoryFeedItemCommands(String q, User user, String categoryName,
            String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchInCategory(q, user, categoryName, period, order,
                pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> searchChannelFeedItemCommands(String q, User user, String channelTitle,
            String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchInChannel(q, channelTitle, user, period, order,
                pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    // public List<FeedItem> findAllFeedsByAllCategories(User user, String
    // categoryName, String period, String order,
    // int pageNumber) {

    // List<FeedItem> feedItems =
    // sortingFeedsService.findFeedItemsByCategoryAndPeriodAndOrderAndPageNumber(user,
    // categoryName, period, order, pageNumber);

    // return feedItems;
    // }

}

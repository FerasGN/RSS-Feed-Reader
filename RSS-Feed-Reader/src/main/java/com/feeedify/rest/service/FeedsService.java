package com.feeedify.rest.service;

import com.feeedify.commands.FeedItemUserCommand;
import com.feeedify.converters.FeedItemUserToFeedItemUserCommand;
import com.feeedify.database.entity.*;
import com.feeedify.database.entity.compositeIds.FeedItemUserId;
import com.feeedify.database.repository.ChannelFeedItemUserRepository;
import com.feeedify.database.repository.FeedItemUserRepository;
import com.feeedify.rest.service.search.FeedSearchingService;
import com.feeedify.rest.service.sortingandfiltering.CategoryFeedsSortAndFilterService;
import com.feeedify.rest.service.sortingandfiltering.SortingAndFilteringFeedsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FeedsService {

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

    public void deleteAllFeeItemUserByUser(User user) {
        List<FeedItemUser> feedItemsUser = feedItemUserRepository.findAllByUser(user);
        feedItemUserRepository.deleteAll(feedItemsUser);
    }

    /**
     * Change the status of liked field. If the value was true make it false and
     * vice versa.
     * 
     * @param userId
     * @param feedItemId
     * @param liked
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
     * Change the status of read field. If the value was true make it false and vice
     * versa.
     * 
     * @param userId
     * @param feedItemId
     * @param read
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

    /**
     * Change the status of read later field. If the value was true make it false
     * and vice versa.
     * 
     * @param userId
     * @param feedItemId
     * @param readLater
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

    public void changeLastReadingTime(Long userId, Long feedItemId, LocalDateTime lastReadingTime) {
        Optional<FeedItemUser> optionalFeedItemUser = feedItemUserRepository
                .findById(new FeedItemUserId(feedItemId, userId));
        if (optionalFeedItemUser.isPresent()) {
            FeedItemUser item = optionalFeedItemUser.get();
            item.setLastReadingDate(lastReadingTime);
            feedItemUserRepository.save(item);
        }
    }

    /**
     * Increases the number of clicks on a feed item.
     * 
     * @param userId
     * @param feedItemId
     */
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
     * Counts the number of all unread feed entries of the given user.
     * 
     * @param user
     * @return Long number of all unread feed entries
     */
    public Long findNumberOfUnreadFeeds(User user) {
        Long count = feedItemUserRepository.countByUserAndRead(user, false);
        return count;
    }

    /**
     * Counts the number of unread feed entries of the given user in a specified
     * category.
     * 
     * @param user
     * @param categoryName
     * @return Long number of unread feed entries in a category
     */
    public Long findNumberOfUnreadFeedsOfCategory(User user, String categoryName) {
        List<FeedItemUser> feedItemsUser = categoryFeedsSortAndFilterService
                .findFeedItemUsersByCategoryNameOrderedyCategoryNameAndPublishLocalDate(user, categoryName, null);

        Long count = feedItemsUser.stream().filter(f -> !f.isRead()).count();
        return count;
    }

    /**
     * Counts the number of unread feed entries of the given user in a specified
     * channel.
     * 
     * @param user
     * @param channelTitle
     * @return Long number of unread feed entries in a channel
     */
    public Long findNumberOfUnreadFeedsOfChannel(User user, String channelTitle) {
        Long count = channelFeedItemUserRepository.countByUserAndFeedItem_Channel_TitleAndRead(user, channelTitle,
                false);
        return count;
    }

    /**
     * Returns a page with filtered by time period and sorted feed items owned by
     * the given user
     *
     * 
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser> List of feed items
     */
    public List<FeedItemUser> findAllFeedItemsUser(User user, String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findAllFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

        return feedItemsUser;
    }

    /**
     * Returns a page with filtered by time period and sorted read later feed items
     * owned by the given user
     * 
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    public List<FeedItemUser> findReadLaterFeedItemsUser(User user, String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findReadLaterFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

        return feedItemsUser;
    }

    /**
     * Returns a page with filtered by time period and sorted liked feed items owned
     * by the given user
     * 
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    public List<FeedItemUser> findLikedFeedItemsUser(User user, String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findLikedFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

        return feedItemsUser;
    }

    public List<FeedItemUser> findRecentlyReadFeedItemsUser(User user, String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findRecentlyReadFeedItemsByPeriodAndOrderAndPageNumber(user, period, order, pageNumber);

        return feedItemsUser;
    }

    /**
     * Returns a page with filtered by time period and sorted feed items of the
     * given category owned by the given user
     * 
     * @param user
     * @param categoryName
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    public List<FeedItemUser> findCategoryFeedItemsUser(User user, String categoryName, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findCategoryFeedItemsByPeriodAndOrderAndPageNumber(user, categoryName, period, order, pageNumber);

        return feedItemsUser;
    }

    /**
     * Returns a page with filtered by time period and sorted feed items of the
     * given channel owned by the given user
     * 
     * @param user
     * @param channelUrl
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    public List<FeedItemUser> findChannelFeedItemsUser(User user, String channelUrl, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = sortingAndFilteringFeedsService
                .findChannelFeedItemsByPeriodAndOrderAndPageNumber(user, channelUrl, period, order, pageNumber);

        return feedItemsUser;
    }

    /**
     * Finds the page with the specified page number that contains sorted feed
     * entries, filtered by time period, and belongs to the given user and Converts
     * the list feedItemUser to a list of feedItemsUserCommand.
     * 
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> findAllFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {

        List<FeedItemUser> feedItemsUser = findAllFeedItemsUser(user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    /**
     * 
     * Finds the page with the specified page number that contains sorted read later
     * feed entries, filtered by time period, and belongs to the given user and
     * Converts the list feedItemUser to a list of feedItemsUserCommand.
     * 
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> findReadLaterFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {

        List<FeedItemUser> feedItemsUser = findReadLaterFeedItemsUser(user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    /**
     * Finds the page with the specified page number that contains sorted liked feed
     * entries, filtered by time period, and belongs to the given user and Converts
     * the list feedItemUser to a list of feedItemsUserCommand.
     * 
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> findLikedFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {

        List<FeedItemUser> feedItemsUser = findLikedFeedItemsUser(user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> findRecentlyReadFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = findRecentlyReadFeedItemsUser(user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    /**
     * Finds the page with the specified page number that contains sorted feed
     * entries of the given category, filtered by time period, and belongs to the
     * given user and Converts the list feedItemUser to a list of
     * feedItemsUserCommand.
     * 
     * @param user
     * @param categoryName
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> findCategoryFeedItemUserCommands(User user, String categoryName, String period,
            String order, int pageNumber) {

        List<FeedItemUser> feedItemsUser = findCategoryFeedItemsUser(user, categoryName, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    /**
     * Finds the page with the specified page number that contains sorted feed
     * entries of the given channel, filtered by time period, and belongs to the
     * given user and Converts the list feedItemUser to a list of
     * feedItemsUserCommand.
     * 
     * @param user
     * @param channelUrl
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> findChannelFeedItemUserCommands(User user, String channelUrl, String period,
            String order, int pageNumber) {

        List<FeedItemUser> feedItemsUser = findChannelFeedItemsUser(user, channelUrl, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    /**
     * Converts a list of FeedItemUser to a list of FeedItemUserCommand
     * 
     * @param user
     * @param feedItemsUser
     * @return List<FeedItemUserCommand>
     */
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

    /**
     * Searches for the given string in all feeds enteries which belong to the given
     * user. The results will be filtered and ordered by the given Criteria.
     * 
     * @param q
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> searchAllFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchAll(q, user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    /**
     * 
     * Searches for the given string in read later feeds enteries which belong to
     * the given user. The results will be filtered and ordered by the given
     * Criteria.
     * 
     * @param q
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> searchReadLaterFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchInReadLater(q, user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    /**
     * Searches for the given string in the liked feeds enteries which belong to the
     * given user. The results will be filtered and ordered by the given Criteria.
     * 
     * @param q
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> searchLikedFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchInLikedFeeds(q, user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    public List<FeedItemUserCommand> searchRecentlyReadFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchInRecentlyReadFeeds(q, user, period, order, pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    /**
     * 
     * Searches for the given string in feeds enteries of a given category which
     * belong to the given user. The results will be filtered and ordered by the
     * given Criteria.
     * 
     * @param q
     * @param user
     * @param categoryName
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> searchCategoryFeedItemCommands(String q, User user, String categoryName,
            String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchInCategory(q, user, categoryName, period, order,
                pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

    /**
     * Searches for the given string in feeds enteries of a given channel which
     * belong to the given user. The results will be filtered and ordered by the
     * given Criteria.
     * 
     * @param q
     * @param user
     * @param channelTitle
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    public List<FeedItemUserCommand> searchChannelFeedItemCommands(String q, User user, String channelTitle,
            String period, String order, int pageNumber) {
        List<FeedItemUser> feedItemsUser = searchingService.searchInChannel(q, channelTitle, user, period, order,
                pageNumber);
        List<FeedItemUserCommand> feedItemsUserCommands = convertFeedItemsUserToFeedItemUserCommands(user,
                feedItemsUser);

        return feedItemsUserCommands;
    }

}

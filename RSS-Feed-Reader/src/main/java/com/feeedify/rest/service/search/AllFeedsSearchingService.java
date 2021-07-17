package com.feeedify.rest.service.search;

import static com.feeedify.constants.Constants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feeedify.database.MaterializedViewManager;
import com.feeedify.database.entity.Channel;
import com.feeedify.database.entity.ChannelUser;
import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.User;
import com.feeedify.database.entity.compositeIds.FeedItemUserId;
import com.feeedify.database.repository.AllFeedItemUserRepository;
import com.feeedify.database.repository.ChannelUserRepository;
import com.feeedify.rest.service.sortingandfiltering.CustomPagination;

@Service
public class AllFeedsSearchingService {

    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private AllFeedItemUserRepository allFeedItemUserRepository;
    @Autowired
    private CustomPagination customPagination;
    @Autowired
    private MaterializedViewManager materializedViewManager;

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItemUser> findFeedItemsUser(String q, User user, String period, String order, int pageNumber) {

        List<FeedItemUser> feedItemsUser = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                // set start date to null when searching in all feeds is needed
                feedItemsUser = findFilteredAndOrderedSearchResults(q, user, null, order, pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItemsUser = findFilteredAndOrderedSearchResults(q, user, today, order, pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItemsUser = findFilteredAndOrderedSearchResults(q, user, dateBeforeSevenDays, order, pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItemsUser = findFilteredAndOrderedSearchResults(q, user, dateBeforeThirtyDays, order, pageNumber);
                break;

            default: {
                // search in all feeds with the given page number
                feedItemsUser = findFilteredAndOrderedSearchResults(q, user, null, order, pageNumber);
                break;
            }
        }
        return feedItemsUser;
    }

    /**
     * @param user
     * @param startDate
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    private List<FeedItemUser> findFilteredAndOrderedSearchResults(String q, User user, LocalDate startDate,
            String order, Integer pageNumber) {

        List<FeedItemUser> feedItemsUsers = searchAndFilterByPublishLocalDate(user.getId(), q, pageNumber, startDate);

        switch (order) {
            case ORDER_BY_LATEST: {
                Comparator<FeedItemUser> compareByPublishDateDesc = Comparator.comparing(
                        f -> f.getFeedItem().getPublishDate(), Comparator.nullsLast(Comparator.reverseOrder()));
                Collections.sort(feedItemsUsers, compareByPublishDateDesc);

                break;
            } // end case

            case ORDER_BY_OLDEST: {
                Comparator<FeedItemUser> compareByPublishDateAsc = Comparator.comparing(
                        f -> f.getFeedItem().getPublishDate(), Comparator.nullsLast(Comparator.naturalOrder()));
                Collections.sort(feedItemsUsers, compareByPublishDateAsc);

                break;
            } // end case
            case ORDER_BY_UNREAD: {
                Comparator<FeedItemUser> compareByUnread = Comparator.comparing(f -> f.isRead());
                Comparator<FeedItemUser> compareByPublishDate = Comparator.comparing(
                        f -> f.getFeedItem().getPublishDate(), Comparator.nullsLast(Comparator.reverseOrder()));
                Comparator<FeedItemUser> compareByUnreadAndPublishDate = compareByUnread
                        .thenComparing(compareByPublishDate);
                Collections.sort(feedItemsUsers, compareByUnreadAndPublishDate);
                break;
            } // end case

            case ORDER_BY_CHANNEL: {
                Comparator<FeedItemUser> compareByChannelTitle = Comparator
                        .comparing(f -> f.getFeedItem().getChannel().getTitle());
                Comparator<FeedItemUser> compareByPublishDate = Comparator.comparing(
                        f -> f.getFeedItem().getPublishDate(), Comparator.nullsLast(Comparator.reverseOrder()));
                Comparator<FeedItemUser> compareByChannelTitleAndPublishDate = compareByChannelTitle
                        .thenComparing(compareByPublishDate);
                Collections.sort(feedItemsUsers, compareByChannelTitleAndPublishDate);
                break;
            } // end case

            case ORDER_BY_ALL_CATEGORIES: {
                feedItemsUsers = findFeedItemUsersOrderedByCategoryName(user, pageNumber, feedItemsUsers);
                break;
            } // end case

            case ORDER_BY_MOST_RELEVANT: {
                return feedItemsUsers;
            } // end case

            default:
                return feedItemsUsers;
        }// end switch

        return feedItemsUsers;
    }

    /**
     * @param userId
     * @param q
     * @param pageNumber
     * @param startDate
     * @return List<FeedItemUser>
     */
    private List<FeedItemUser> searchAndFilterByPublishLocalDate(Long userId, String q, Integer pageNumber,
            LocalDate startDate) {
        List<FeedItemUser> feedItemsUsers = search(userId, q, pageNumber);
        if (startDate == null)
            return feedItemsUsers;
        else {
            feedItemsUsers = feedItemsUsers.stream().filter(f -> {
                if (f.getFeedItem().getPublishLocalDate() != null)
                    return f.getFeedItem().getPublishLocalDate().isAfter(startDate)
                            || f.getFeedItem().getPublishLocalDate().isEqual(startDate);
                return false;
            }).collect(Collectors.toList());
        }
        return feedItemsUsers;
    }

    /**
     * @param user
     * @param pageNumber
     * @param feedItemsUsers
     * @return List<FeedItemUser>
     */
    private List<FeedItemUser> findFeedItemUsersOrderedByCategoryName(User user, Integer pageNumber,
            List<FeedItemUser> feedItemsUsers) {

        Map<String, List<FeedItemUser>> feedItemUsersOfCategoryMap = new HashMap<String, List<FeedItemUser>>();

        feedItemsUsers.forEach(fu -> {
            Channel channel = fu.getFeedItem().getChannel();
            ChannelUser channelUser = channelUserRepository.findByUserAndChannel(user, channel);
            String categoryNameOfFeedItemUser = channelUser.getCategory().getName();
            feedItemUsersOfCategoryMap.computeIfAbsent(categoryNameOfFeedItemUser, k -> new ArrayList<>()).add(fu);
        });

        List<FeedItemUser> result = new ArrayList<>();
        feedItemUsersOfCategoryMap.values().forEach(list -> result.addAll(list));

        return result;
    }

    /**
     * @param userId
     * @param query
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    private List<FeedItemUser> search(Long userId, String query, Integer pageNumber) {

        List<Long> feedItemsIds = materializedViewManager.fullTextSearchFeedItem(query);
        List<Long> feedItemsIdsPage = customPagination.getNextPageOfFeedItemsIds(pageNumber, PAGE_SIZE, feedItemsIds);

        List<FeedItemUserId> feedItemUsersIds = feedItemsIdsPage.stream().filter(feedItemId -> {
            Optional<FeedItemUser> opitonalFeedItemUser = allFeedItemUserRepository
                    .findById(new FeedItemUserId(feedItemId, userId));
            return opitonalFeedItemUser.isPresent();
        }).map(feedItemId -> new FeedItemUserId(feedItemId, userId)).collect(Collectors.toList());

        List<FeedItemUser> feedItemUsers = feedItemUsersIds.stream().map(feedItemUsersId -> {
            Optional<FeedItemUser> opitonalFeedItemUser = allFeedItemUserRepository.findById(feedItemUsersId);
            return opitonalFeedItemUser.get();
        }).collect(Collectors.toList());

        return feedItemUsers;
    }

}

package com.feeedify.rest.service.search;

import static com.feeedify.constants.Constants.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feeedify.database.MaterializedViewManager;

import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.User;
import com.feeedify.database.entity.compositeIds.FeedItemUserId;
import com.feeedify.database.repository.ChannelFeedItemUserRepository;
import com.feeedify.rest.service.sortingandfiltering.CustomPagination;

@Service
public class ChannelFeedsSearchingService {

    @Autowired
    private ChannelFeedItemUserRepository channelFeedItemUserRepository;
    @Autowired
    private CustomPagination customPagination;
    @Autowired
    private MaterializedViewManager materializedViewManager;

    public List<FeedItemUser> findFeedItemsUser(String q, User user, String channelTitle, String period, String order,
            int pageNumber) {

        List<FeedItemUser> feedItemsUser = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                // set start date to null when all feeds are needed
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, channelTitle, null, order, pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, channelTitle, today, order, pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, channelTitle, dateBeforeSevenDays, order,
                        pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, channelTitle, dateBeforeThirtyDays, order,
                        pageNumber);
                break;

            default: {
                // find all read later feeds with the given page number
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, channelTitle, null, order, pageNumber);
                break;
            }
        }
        return feedItemsUser;
    }

    private List<FeedItemUser> findFilteredAndOrderedFeedItemsUser(String q, User user, String channelTitle,
            LocalDate startDate, String order, Integer pageNumber) {

        List<FeedItemUser> feedItemsUser = searchAndFilterByPublishLocalDate(user.getId(), channelTitle, q, pageNumber,
                startDate);

        switch (order) {
            case ORDER_BY_LATEST: {
                Comparator<FeedItemUser> compareByPublishDateDesc = Comparator.comparing(
                        f -> f.getFeedItem().getPublishDate(), Comparator.nullsLast(Comparator.reverseOrder()));
                Collections.sort(feedItemsUser, compareByPublishDateDesc);
                break;
            } // end case

            case ORDER_BY_OLDEST: {
                Comparator<FeedItemUser> compareByPublishDateAsc = Comparator.comparing(
                        f -> f.getFeedItem().getPublishDate(), Comparator.nullsLast(Comparator.naturalOrder()));

                Collections.sort(feedItemsUser, compareByPublishDateAsc);
                break;
            } // end case

            case ORDER_BY_UNREAD: {
                Comparator<FeedItemUser> compareByUnread = Comparator.comparing(f -> f.isRead());
                Comparator<FeedItemUser> compareByPublishDate = Comparator.comparing(
                        f -> f.getFeedItem().getPublishDate(), Comparator.nullsLast(Comparator.reverseOrder()));
                Comparator<FeedItemUser> compareByUnreadAndPublishDate = compareByUnread
                        .thenComparing(compareByPublishDate);
                Collections.sort(feedItemsUser, compareByUnreadAndPublishDate);
                break;
            } // end case

            case ORDER_BY_MOST_RELEVANT: {
                return feedItemsUser;
            } // end case

            default:
                return feedItemsUser;
        }// end switch

        return feedItemsUser;
    }

    private List<FeedItemUser> searchAndFilterByPublishLocalDate(Long userId, String cahnnelTitle, String q,
            Integer pageNumber, LocalDate startDate) {
        List<FeedItemUser> feedItemsUsers = search(userId, cahnnelTitle, q, pageNumber);
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

    private List<FeedItemUser> search(Long userId, String channelTitle, String query, Integer pageNumber) {

        List<Long> feedItemsIds = materializedViewManager.fullTextSearchFeedItem(query);
        List<Long> feedItemsIdsPage = customPagination.getNextPageOfFeedItemsIds(pageNumber, PAGE_SIZE, feedItemsIds);

        List<FeedItemUserId> feedItemUsersIds = feedItemsIdsPage.stream().filter(feedItemId -> {
            Optional<FeedItemUser> opitonalFeedItemUser = channelFeedItemUserRepository
                    .findByIdAndFeedItem_Channel_Title(new FeedItemUserId(feedItemId, userId), channelTitle);
            return opitonalFeedItemUser.isPresent();
        }).map(feedItemId -> new FeedItemUserId(feedItemId, userId)).collect(Collectors.toList());

        List<FeedItemUser> feedItemUsers = feedItemUsersIds.stream().map(feedItemUsersId -> {
            Optional<FeedItemUser> opitonalFeedItemUser = channelFeedItemUserRepository
                    .findByIdAndFeedItem_Channel_Title(feedItemUsersId, channelTitle);
            return opitonalFeedItemUser.get();
        }).collect(Collectors.toList());

        return feedItemUsers;
    }

}

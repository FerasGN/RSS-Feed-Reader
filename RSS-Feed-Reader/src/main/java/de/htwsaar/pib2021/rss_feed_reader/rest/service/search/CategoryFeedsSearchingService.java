package de.htwsaar.pib2021.rss_feed_reader.rest.service.search;

import static de.htwsaar.pib2021.rss_feed_reader.constants.Constants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.MaterializedViewManager;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.compositeIds.FeedItemUserId;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.AllFeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering.CustomPagination;

@Service
public class CategoryFeedsSearchingService {

    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private AllFeedItemUserRepository allFeedItemUserRepository;
    @Autowired
    private CustomPagination customPagination;
    @Autowired
    private MaterializedViewManager materializedViewManager;

    public List<FeedItemUser> findFeedItemsUser(String q, User user, String categoryName, String period, String order,
            Integer pageNumber) {

        List<FeedItemUser> feedItemsUser = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, categoryName, null, order, pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, categoryName, today, order, pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, categoryName, dateBeforeSevenDays, order,
                        pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, categoryName, dateBeforeThirtyDays, order,
                        pageNumber);
                break;

            default: {
                // find all feeds with the given page number
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(q, user, categoryName, null, order, pageNumber);
                break;
            }
        }
        return feedItemsUser;
    }

    private List<FeedItemUser> findFilteredAndOrderedFeedItemsUser(String q, User user, String categoryName,
            LocalDate startDate, String order, Integer pageNumber) {

        List<FeedItemUser> feedItemsUser = searchAndFilterByPublishLocalDate(user.getId(), q, pageNumber, startDate);
        feedItemsUser = findFeedItemUsersByCategoryNameOrderedByCategoryName(user, categoryName, feedItemsUser);

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

            case ORDER_BY_CHANNEL: {
                Comparator<FeedItemUser> compareByChannelTitle = Comparator
                        .comparing(f -> f.getFeedItem().getChannel().getTitle());
                Comparator<FeedItemUser> compareByPublishDate = Comparator.comparing(
                        f -> f.getFeedItem().getPublishDate(), Comparator.nullsLast(Comparator.reverseOrder()));
                Comparator<FeedItemUser> compareByChannelTitleAndPublishDate = compareByChannelTitle
                        .thenComparing(compareByPublishDate);
                Collections.sort(feedItemsUser, compareByChannelTitleAndPublishDate);
                break;
            } // end case

            case ORDER_BY_MOST_RELEVANT: {
                return feedItemsUser;
            } // end case

            default:
                break;
        }// end switch

        return feedItemsUser;
    }

    private List<FeedItemUser> searchAndFilterByPublishLocalDate(Long userId, String q, Integer pageNumber,
            LocalDate startDate) {
        List<FeedItemUser> feedItemsUser = search(userId, q, pageNumber);
        if (startDate == null)
            return feedItemsUser;
        else {
            feedItemsUser = feedItemsUser.stream().filter(f -> {
                if (f.getFeedItem().getPublishLocalDate() != null)
                    return f.getFeedItem().getPublishLocalDate().isAfter(startDate)
                            || f.getFeedItem().getPublishLocalDate().isEqual(startDate);
                return false;
            }).collect(Collectors.toList());
        }
        return feedItemsUser;
    }

    private List<FeedItemUser> findFeedItemUsersByCategoryNameOrderedByCategoryName(User user, String categoryName,
            List<FeedItemUser> feedItemsUser) {

        Map<String, List<FeedItemUser>> feedItemUsersOfCategoryMap = new HashMap<String, List<FeedItemUser>>();
        feedItemsUser.forEach(fu -> {
            Channel channel = fu.getFeedItem().getChannel();
            ChannelUser channelUser = channelUserRepository.findByUserAndChannel(user, channel);
            String categoryNameOfFeedItemUser = channelUser.getCategory().getName();
            feedItemUsersOfCategoryMap.computeIfAbsent(categoryNameOfFeedItemUser, k -> new ArrayList<>()).add(fu);
        });

        List<FeedItemUser> result = new ArrayList<>();
        for (Entry<String, List<FeedItemUser>> es : feedItemUsersOfCategoryMap.entrySet()) {
            if (categoryName.equalsIgnoreCase(es.getKey()))
                result.addAll(es.getValue());
        }

        return result;
    }

    private List<FeedItemUser> search(Long userId, String term, Integer pageNumber) {

        List<Long> feedItemsIds = materializedViewManager.fullTextSeachrFeedItem(term);
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

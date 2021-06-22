package de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering;

import static de.htwsaar.pib2021.rss_feed_reader.constants.Constants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.AllFeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;

@Service
public class CategoryFeedsSortAndFilterService {

    private ChannelUserRepository channelUserRepository;
    private AllFeedItemUserRepository allFeedItemUserRepository;
    private CustomPagination customPagination;

    public CategoryFeedsSortAndFilterService(ChannelUserRepository channelUserRepository,
            AllFeedItemUserRepository allFeedItemUserRepository, CustomPagination customPagination) {
        this.channelUserRepository = channelUserRepository;
        this.allFeedItemUserRepository = allFeedItemUserRepository;
        this.customPagination = customPagination;
    }

    public List<FeedItem> findFeedItems(User user, String categoryName, String period, String order,
            Integer pageNumber) {

        List<FeedItem> feedItems = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                // set start date to null when all feeds are needed
                feedItems = findFilteredAndOrderedFeedItems(user, categoryName, null, order, pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItems = findFilteredAndOrderedFeedItems(user, categoryName, today, order, pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItems = findFilteredAndOrderedFeedItems(user, categoryName, dateBeforeSevenDays, order, pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItems = findFilteredAndOrderedFeedItems(user, categoryName, dateBeforeThirtyDays, order,
                        pageNumber);
                break;

            default: {
                // find all feeds with the given page number
                feedItems = findFilteredAndOrderedFeedItems(user, categoryName, null, order, pageNumber);
                break;
            }
        }
        return feedItems;
    }

    private List<FeedItem> findFilteredAndOrderedFeedItems(User user, String categoryName, LocalDate startDate,
            String order, Integer pageNumber) {

        List<FeedItemUser> feedItemsUsers = findFeedItemUsersOrderedyCategoryNameAndPublishLocalDate(user, categoryName,
                startDate);

        switch (order) {
            case ORDER_BY_LATEST: {
                Collections.sort(feedItemsUsers, (a, b) -> {
                    if (b.getFeedItem().getPublishDate() != null && a.getFeedItem().getPublishDate() != null)
                        return b.getFeedItem().getPublishDate().compareTo(a.getFeedItem().getPublishDate());
                    else
                        return 0;
                });
                break;
            } // end case

            case ORDER_BY_OLDEST: {
                Collections.sort(feedItemsUsers, (a, b) -> {
                    if (a.getFeedItem().getPublishDate() != null && b.getFeedItem().getPublishDate() != null)
                        return a.getFeedItem().getPublishDate().compareTo(b.getFeedItem().getPublishDate());
                    else
                        return 0;
                });
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

            default:
                break;
        }// end switch

        List<FeedItem> feedItems = customPagination.getNextPageOfFeedItems(pageNumber, PAGE_SIZE, feedItemsUsers);

        return feedItems;
    }

    private List<FeedItemUser> findFeedItemUsersOrderedyCategoryNameAndPublishLocalDate(User user, String categoryName,
            LocalDate startDate) {
        List<ChannelUser> channelsUser;

        channelsUser = channelUserRepository.findAllByUserAndCategory_NameOrderByCategory_Name(user,
                categoryName.trim().toLowerCase());
        List<FeedItemUser> feedItemsUser = new ArrayList<>();

        // no start date means we need all feeds of a category
        if (startDate == null)
            channelsUser.forEach(cu -> feedItemsUser
                    .addAll(allFeedItemUserRepository.findByUserAndFeedItem_Channel(user, cu.getChannel())));
        else
            channelsUser.forEach(cu -> feedItemsUser.addAll(
                    allFeedItemUserRepository.findByUserAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(
                            user, cu.getChannel(), startDate)));
        return feedItemsUser;
    }
}

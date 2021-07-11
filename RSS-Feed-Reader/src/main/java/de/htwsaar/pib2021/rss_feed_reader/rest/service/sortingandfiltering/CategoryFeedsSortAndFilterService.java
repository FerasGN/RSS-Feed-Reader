package de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering;

import static de.htwsaar.pib2021.rss_feed_reader.constants.Constants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
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

    public List<FeedItemUser> findFeedItemsUser(User user, String categoryName, String period, String order,
            Integer pageNumber) {

        List<FeedItemUser> feedItemsUser = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                // set start date to null when all feeds are needed
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, categoryName, null, order, pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, categoryName, today, order, pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, categoryName, dateBeforeSevenDays, order,
                        pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, categoryName, dateBeforeThirtyDays, order,
                        pageNumber);
                break;

            default: {
                // find all feeds with the given page number
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, categoryName, null, order, pageNumber);
                break;
            }
        }
        return feedItemsUser;
    }

    private List<FeedItemUser> findFilteredAndOrderedFeedItemsUser(User user, String categoryName, LocalDate startDate,
            String order, Integer pageNumber) {

        List<FeedItemUser> feedItemsUsers = findFeedItemUsersByCategoryNameOrderedyCategoryNameAndPublishLocalDate(user,
                categoryName, startDate);

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

            case ORDER_BY_MOST_RELEVANT: {

                break;
            } // end case

            default:
                break;
        }// end switch

        List<FeedItemUser> feedItemsUser = customPagination.getNextPageOfFeedItems(pageNumber, PAGE_SIZE,
                feedItemsUsers);

        return feedItemsUser;
    }

    public List<FeedItemUser> findFeedItemUsersByCategoryNameOrderedyCategoryNameAndPublishLocalDate(User user,
            String categoryName, LocalDate startDate) {
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

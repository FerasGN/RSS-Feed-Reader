package de.htwsaar.pib2021.rss_feed_reader.rest.service.sorting;

import static de.htwsaar.pib2021.rss_feed_reader.constants.Constants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;

@Service
public class CategoryFeedsSortService {

    private ChannelUserRepository channelUserRepository;
    private FeedItemUserRepository feedItemUserRepository;

    public CategoryFeedsSortService(ChannelUserRepository channelUserRepository,
            FeedItemUserRepository feedItemUserRepository) {
        this.channelUserRepository = channelUserRepository;
        this.feedItemUserRepository = feedItemUserRepository;
    }

    public List<FeedItem> findCategoryFeedItemsByPeriodAndOrderAndPageNumber(User user, String categoryName,
            String period, String order, Integer pageNumber) {

        List<FeedItem> feedItems = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                // set start date to null when all feeds are needed
                feedItems = findCategoryFeedItemsByStartDateAndOrderAndPageNumber(user, categoryName, null, order,
                        pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItems = findCategoryFeedItemsByStartDateAndOrderAndPageNumber(user, categoryName, today, order,
                        pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItems = findCategoryFeedItemsByStartDateAndOrderAndPageNumber(user, categoryName,
                        dateBeforeSevenDays, order, pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItems = findCategoryFeedItemsByStartDateAndOrderAndPageNumber(user, categoryName,
                        dateBeforeThirtyDays, order, pageNumber);
                break;

            default: {
                // find all feeds with the given page number
                feedItems = findCategoryFeedItemsByStartDateAndOrderAndPageNumber(user, categoryName, null, order,
                        pageNumber);
                break;
            }
        }
        return feedItems;
    }

    private List<FeedItem> findCategoryFeedItemsByStartDateAndOrderAndPageNumber(User user, String categoryName,
            LocalDate startDate, String order, Integer pageNumber) {
        List<FeedItemUser> feedItemsUsers = Collections.emptyList();

        feedItemsUsers = findFeedItemsUsersFilteredAndOrdered(user, categoryName, startDate, order);

        List<FeedItem> feedItems = getNextPageOfFeedItemsManually(pageNumber, feedItemsUsers);

        return feedItems;
    }

    private List<FeedItem> getNextPageOfFeedItemsManually(Integer pageNumber, List<FeedItemUser> feedItemsUsers) {
        List<FeedItem> feedItems = feedItemsUsers.stream().map((FeedItemUser e) -> e.getFeedItem())
                .collect(Collectors.toList());

        double lastPage = Math.ceil(feedItems.size() / (double) PAGE_SIZE);
        int nextPage = (pageNumber * PAGE_SIZE) + PAGE_SIZE;

        // feeds number is less than the page size
        if (pageNumber < lastPage && feedItems.size() < PAGE_SIZE)
            return feedItems;
        // next feeds page index is greater than feeditems last index
        else if (pageNumber < lastPage && nextPage > feedItems.size())
            feedItems = feedItems.subList(pageNumber * PAGE_SIZE, feedItems.size());
        else if (pageNumber < lastPage)
            feedItems = feedItems.subList(pageNumber * PAGE_SIZE, (pageNumber * PAGE_SIZE) + PAGE_SIZE);

        else
            feedItems = Collections.emptyList();

        return feedItems;
    }

    private List<FeedItemUser> findFeedItemsUsersFilteredAndOrdered(User user, String categoryName, LocalDate startDate,
            String order) {

        List<FeedItemUser> feedItemsUser = findFeedItemUsersOrderedyCategoryNameAndPublishLocalDate(user, categoryName,
                startDate);

        switch (order) {
            case ORDER_BY_LATEST: {
                Collections.sort(feedItemsUser, (a, b) -> {
                    if (b.getFeedItem().getPublishDate() != null && a.getFeedItem().getPublishDate() != null)
                        return b.getFeedItem().getPublishDate().compareTo(a.getFeedItem().getPublishDate());
                    else
                        return 0;
                });
                break;
            } // end case

            case ORDER_BY_OLDEST: {
                Collections.sort(feedItemsUser, (a, b) -> {
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

            default:
                break;
        }// end switch

        return feedItemsUser;
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
                    .addAll(feedItemUserRepository.findByUserAndFeedItem_Channel(user, cu.getChannel())));
        else
            channelsUser.forEach(cu -> feedItemsUser.addAll(
                    feedItemUserRepository.findByUserAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(
                            user, cu.getChannel(), startDate)));
        return feedItemsUser;
    }
}

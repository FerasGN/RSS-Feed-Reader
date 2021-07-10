package de.htwsaar.pib2021.rss_feed_reader.rest.service.sortingandfiltering;

import static de.htwsaar.pib2021.rss_feed_reader.constants.Constants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
import de.htwsaar.pib2021.rss_feed_reader.database.repository.AllFeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;

@Service
public class AllFeedsSortAndFilterService {

    private ChannelUserRepository channelUserRepository;
    private AllFeedItemUserRepository allFeedItemUserRepository;
    private CustomPagination customPagination;

    public AllFeedsSortAndFilterService(ChannelUserRepository channelUserRepository,
            AllFeedItemUserRepository allFeedItemUserRepository, CustomPagination customPagination) {
        this.channelUserRepository = channelUserRepository;
        this.allFeedItemUserRepository = allFeedItemUserRepository;
        this.customPagination = customPagination;
    }

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItemUser> findFeedItemsUser(User user, String period, String order, int pageNumber) {

        List<FeedItemUser> feedItemsUser = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                // set start date to null when all feeds are needed
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, null, order, pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, today, order, pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, dateBeforeSevenDays, order, pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, dateBeforeThirtyDays, order, pageNumber);
                break;

            default: {
                // find all feeds with the given page number
                feedItemsUser = findFilteredAndOrderedFeedItemsUser(user, null, order, pageNumber);
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
    private List<FeedItemUser> findFilteredAndOrderedFeedItemsUser(User user, LocalDate startDate, String order,
            Integer pageNumber) {

        List<FeedItemUser> feedItemsUsers = Collections.emptyList();
        Pageable pageable = null;
        Page<FeedItemUser> page = null;

        switch (order) {
            case ORDER_BY_LATEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                // no start date means we need all feeds
                if (startDate == null)
                    page = allFeedItemUserRepository.findByUserOrderByFeedItem_PublishDateDesc(user, pageable);
                else
                    page = allFeedItemUserRepository
                            .findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(user,
                                    startDate, pageable);

                feedItemsUsers = page.getContent();
                break;
            } // end case

            case ORDER_BY_OLDEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                if (startDate == null)
                    page = allFeedItemUserRepository.findByUserOrderByFeedItem_PublishDateAsc(user, pageable);
                else
                    page = allFeedItemUserRepository
                            .findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(user,
                                    startDate, pageable);

                feedItemsUsers = page.getContent();
                break;
            } // end case
            case ORDER_BY_UNREAD: {

                break;
            } // end case

            case ORDER_BY_CHANNEL: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                if (startDate == null)
                    page = allFeedItemUserRepository
                            .findByUserOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(user, pageable);
                else
                    page = allFeedItemUserRepository
                            .findByUserAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(
                                    user, startDate, pageable);

                feedItemsUsers = page.getContent();
                break;
            } // end case

            case ORDER_BY_ALL_CATEGORIES: {
                // order by specific category or all categories is complex -> we implement the
                // paging for it ourselves
                feedItemsUsers = findFeedItemUsersOrderedByCategoryNameAndPublishLocalDate(user, startDate);
                feedItemsUsers = customPagination.getNextPageOfFeedItems(pageNumber, PAGE_SIZE, feedItemsUsers);
                break;
            } // end case

            case ORDER_BY_MOST_RELEVANT: {

                break;
            } // end case

            default:
                return feedItemsUsers;
        }// end switch

        return feedItemsUsers;
    }

    /**
     * @param user
     * @param categoryName
     * @param startDate
     * @return List<FeedItemUser>
     */
    private List<FeedItemUser> findFeedItemUsersOrderedByCategoryNameAndPublishLocalDate(User user,
            LocalDate startDate) {
        List<ChannelUser> channelsUser;

        channelsUser = channelUserRepository.findAllByUserOrderByCategory_Name(user);

        List<FeedItemUser> feedItemsUser = new ArrayList<>();

        // no start date means we need all feeds of a category
        if (startDate == null)
            channelsUser.forEach(cu -> feedItemsUser
                    .addAll(allFeedItemUserRepository.findByUserAndFeedItem_Channel(user, cu.getChannel())));
        else {
            channelsUser.forEach(cu -> feedItemsUser.addAll(
                    allFeedItemUserRepository.findByUserAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(
                            user, cu.getChannel(), startDate)));
        }
        return feedItemsUser;
    }
}

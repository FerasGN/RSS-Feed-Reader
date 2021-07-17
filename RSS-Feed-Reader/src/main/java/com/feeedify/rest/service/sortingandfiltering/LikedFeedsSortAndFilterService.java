package com.feeedify.rest.service.sortingandfiltering;

import static com.feeedify.constants.Constants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.feeedify.database.entity.ChannelUser;
import com.feeedify.database.entity.FeedItemUser;
import com.feeedify.database.entity.User;
import com.feeedify.database.repository.ChannelUserRepository;

import com.feeedify.database.repository.LikedFeedItemUserRepository;

@Service
public class LikedFeedsSortAndFilterService {

    @Autowired
    private ChannelUserRepository channelUserRepository;
    @Autowired
    private LikedFeedItemUserRepository likedFeedItemUserRepository;
    @Autowired
    private CustomPagination customPagination;

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
                // find all read later feeds with the given page number
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

        List<FeedItemUser> feedItemsUser = Collections.emptyList();
        Pageable pageable = null;
        Page<FeedItemUser> page = null;

        switch (order) {
            case ORDER_BY_LATEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                // no start date means we need all feeds
                if (startDate == null)
                    page = likedFeedItemUserRepository.findByUserAndLikedOrderByFeedItem_PublishDateDesc(user, true,
                            pageable);
                else
                    page = likedFeedItemUserRepository
                            .findByUserAndLikedAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(
                                    user, true, startDate, pageable);

                feedItemsUser = page.getContent();
                break;
            } // end case

            case ORDER_BY_OLDEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                if (startDate == null)
                    page = likedFeedItemUserRepository.findByUserAndLikedOrderByFeedItem_PublishDateAsc(user, true,
                            pageable);
                else
                    page = likedFeedItemUserRepository
                            .findByUserAndLikedAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(
                                    user, true, startDate, pageable);

                feedItemsUser = page.getContent();
                break;
            } // end case

            case ORDER_BY_UNREAD: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                if (startDate == null)
                    page = likedFeedItemUserRepository.findByUserAndLikedOrderByReadAscFeedItem_PublishDateDesc(user,
                            true, pageable);
                else
                    page = likedFeedItemUserRepository
                            .findByUserAndLikedAndFeedItem_publishLocalDateGreaterThanEqualOrderByReadAscFeedItem_PublishDateDesc(
                                    user, true, startDate, pageable);

                feedItemsUser = page.getContent();

                break;
            } // end case

            case ORDER_BY_CHANNEL: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                if (startDate == null)
                    page = likedFeedItemUserRepository
                            .findByUserAndLikedOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(user, true,
                                    pageable);
                else
                    page = likedFeedItemUserRepository
                            .findByUserAndLikedAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(
                                    user, true, startDate, pageable);

                feedItemsUser = page.getContent();
                break;
            } // end case

            case ORDER_BY_ALL_CATEGORIES: {
                // order by specific category or all categories is complex -> we implement the
                // paging for it ourselves
                feedItemsUser = findFeedItemUsersOrderedByCategoryNameAndPublishLocalDate(user, startDate);
                feedItemsUser = customPagination.getNextPageOfFeedItems(pageNumber, PAGE_SIZE, feedItemsUser);
                break;
            } // end case

            case ORDER_BY_MOST_RELEVANT: {

                break;
            } // end case

            default:
                return feedItemsUser;
        }// end switch

        return feedItemsUser;
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
            channelsUser.forEach(cu -> feedItemsUser.addAll(
                    likedFeedItemUserRepository.findByUserAndLikedAndFeedItem_Channel(user, true, cu.getChannel())));
        else
            channelsUser.forEach(cu -> feedItemsUser.addAll(likedFeedItemUserRepository
                    .findByUserAndLikedAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(user, true,
                            cu.getChannel(), startDate)));
        return feedItemsUser;
    }
}

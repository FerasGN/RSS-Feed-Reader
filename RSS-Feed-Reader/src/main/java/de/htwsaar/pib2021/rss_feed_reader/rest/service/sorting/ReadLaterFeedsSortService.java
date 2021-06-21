package de.htwsaar.pib2021.rss_feed_reader.rest.service.sorting;

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
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.FeedItemUserRepository;

@Service
public class ReadLaterFeedsSortService {

    private ChannelUserRepository channelUserRepository;
    private FeedItemUserRepository feedItemUserRepository;

    public ReadLaterFeedsSortService(ChannelUserRepository channelUserRepository,
            FeedItemUserRepository feedItemUserRepository) {
        this.channelUserRepository = channelUserRepository;
        this.feedItemUserRepository = feedItemUserRepository;
    }

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItem> findReadLaterFeedItemsByPeriodAndOrderAndPageNumber(User user, String period, String order,
            int pageNumber) {

        List<FeedItem> feedItems = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                // set start date to null when all feeds are needed
                feedItems = findReadLaterFeedItemsByStartDateAndOrderAndPageNumber(user, null, order, pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItems = findReadLaterFeedItemsByStartDateAndOrderAndPageNumber(user, today, order, pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItems = findReadLaterFeedItemsByStartDateAndOrderAndPageNumber(user, dateBeforeSevenDays, order,
                        pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItems = findReadLaterFeedItemsByStartDateAndOrderAndPageNumber(user, dateBeforeThirtyDays, order,
                        pageNumber);
                break;

            default: {
                // find all read later feeds with the given page number
                feedItems = findReadLaterFeedItemsByStartDateAndOrderAndPageNumber(user, null, order, pageNumber);
                break;
            }
        }
        return feedItems;
    }

    /**
     * @param user
     * @param startDate
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    private List<FeedItem> findReadLaterFeedItemsByStartDateAndOrderAndPageNumber(User user, LocalDate startDate,
            String order, Integer pageNumber) {

        List<FeedItem> feedItems = findFilteredAndOrderedReadLaterFeedItems(user, startDate, order, pageNumber);

        return feedItems;
    }

    /**
     * @param user
     * @param startDate
     * @param order
     * @param pageNumber
     * @return List<FeedItemUser>
     */
    private List<FeedItem> findFilteredAndOrderedReadLaterFeedItems(User user, LocalDate startDate, String order,
            Integer pageNumber) {

        List<FeedItemUser> feedItemsUsers = Collections.emptyList();
        List<FeedItem> feedItems = Collections.emptyList();
        Pageable pageable = null;
        Page<FeedItemUser> page = null;

        switch (order) {
            case ORDER_BY_LATEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                // no start date means we need all feeds
                if (startDate == null)
                    page = feedItemUserRepository.findByUserAndReadLaterOrderByFeedItem_PublishDateDesc(user, true,
                            pageable);
                else
                    page = feedItemUserRepository
                            .findByUserAndReadLaterAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(
                                    user, true, startDate, pageable);

                feedItemsUsers = page.getContent();
                feedItems = feedItemsUsers.stream().map((FeedItemUser e) -> e.getFeedItem())
                        .collect(Collectors.toList());
                break;
            } // end case

            case ORDER_BY_OLDEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                if (startDate == null)
                    page = feedItemUserRepository.findByUserAndReadLaterOrderByFeedItem_PublishDateAsc(user, true,
                            pageable);
                else
                    page = feedItemUserRepository
                            .findByUserAndReadLaterAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(
                                    user, true, startDate, pageable);

                feedItemsUsers = page.getContent();
                feedItems = feedItemsUsers.stream().map((FeedItemUser e) -> e.getFeedItem())
                        .collect(Collectors.toList());
                break;
            } // end case

            case ORDER_BY_CHANNEL: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                if (startDate == null)
                    page = feedItemUserRepository
                            .findByUserAndReadLaterOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(user, true,
                                    pageable);
                else
                    page = feedItemUserRepository
                            .findByUserAndReadLaterAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_Channel_TitleAscFeedItem_PublishDateDesc(
                                    user, true, startDate, pageable);

                feedItemsUsers = page.getContent();
                feedItems = feedItemsUsers.stream().map((FeedItemUser e) -> e.getFeedItem())
                        .collect(Collectors.toList());
                break;
            } // end case

            case ORDER_BY_ALL_CATEGORIES: {
                // order by specific category or all categories is complex -> we implement the
                // paging for it ourselves
                feedItemsUsers = findFeedItemUsersOrderedByCategoryNameAndPublishLocalDate(user, startDate);
                feedItems = getNextPageOfFeedItemsManually(pageNumber, feedItemsUsers);
                break;
            } // end case

            default:
                return feedItems;
        }// end switch

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
                    feedItemUserRepository.findByUserAndReadLaterAndFeedItem_Channel(user, true, cu.getChannel())));
        else
            channelsUser.forEach(cu -> feedItemsUser.addAll(feedItemUserRepository
                    .findByUserAndReadLaterAndFeedItem_ChannelAndFeedItem_publishLocalDateGreaterThanEqual(user, true,
                            cu.getChannel(), startDate)));
        return feedItemsUser;
    }
}

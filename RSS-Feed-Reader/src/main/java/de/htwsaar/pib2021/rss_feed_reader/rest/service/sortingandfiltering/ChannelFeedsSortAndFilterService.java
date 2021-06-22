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

import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItemUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelFeedItemUserRepository;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.ChannelUserRepository;

@Service
public class ChannelFeedsSortAndFilterService {

    private ChannelFeedItemUserRepository channelFeedItemUserRepository;

    public ChannelFeedsSortAndFilterService(ChannelFeedItemUserRepository channelFeedItemUserRepository) {
        this.channelFeedItemUserRepository = channelFeedItemUserRepository;
    }

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItem>
     */
    public List<FeedItem> findFeedItems(User user, String channelTitle, String period, String order, int pageNumber) {

        List<FeedItem> feedItems = Collections.emptyList();

        switch (period) {
            case PERIOD_ALL:
                // set start date to null when all feeds are needed
                feedItems = findFilteredAndOrderedFeedItems(user, channelTitle, null, order, pageNumber);
                break;

            case PERIOD_TODAY:
                LocalDate today = LocalDate.now();
                feedItems = findFilteredAndOrderedFeedItems(user, channelTitle, today, order, pageNumber);
                break;

            case PERIOD_LAST_SEVEN_DAYS:
                LocalDate dateBeforeSevenDays = LocalDate.now().minusDays(7L);
                feedItems = findFilteredAndOrderedFeedItems(user, channelTitle, dateBeforeSevenDays, order, pageNumber);
                break;

            case PERIOD_LAST_THIRTY_DAYS:
                LocalDate dateBeforeThirtyDays = LocalDate.now().minusDays(30L);
                feedItems = findFilteredAndOrderedFeedItems(user, channelTitle, dateBeforeThirtyDays, order,
                        pageNumber);
                break;

            default: {
                // find all read later feeds with the given page number
                feedItems = findFilteredAndOrderedFeedItems(user, channelTitle, null, order, pageNumber);
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
     * @return List<FeedItemUser>
     */
    private List<FeedItem> findFilteredAndOrderedFeedItems(User user, String channelTitle, LocalDate startDate,
            String order, Integer pageNumber) {

        List<FeedItemUser> feedItemsUsers = Collections.emptyList();
        List<FeedItem> feedItems = Collections.emptyList();
        Pageable pageable = null;
        Page<FeedItemUser> page = null;

        switch (order) {
            case ORDER_BY_LATEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                // no start date means we need all feeds
                if (startDate == null)
                    page = channelFeedItemUserRepository
                            .findByUserAndFeedItem_Channel_TitleOrderByFeedItem_PublishDateDesc(user, channelTitle,
                                    pageable);
                else
                    page = channelFeedItemUserRepository
                            .findByUserAndFeedItem_Channel_TitleAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateDesc(
                                    user, channelTitle, startDate, pageable);

                feedItemsUsers = page.getContent();
                feedItems = feedItemsUsers.stream().map((FeedItemUser e) -> e.getFeedItem())
                        .collect(Collectors.toList());
                break;
            } // end case

            case ORDER_BY_OLDEST: {
                pageable = PageRequest.of(pageNumber, PAGE_SIZE);

                if (startDate == null)
                    page = channelFeedItemUserRepository
                            .findByUserAndFeedItem_Channel_TitleOrderByFeedItem_PublishDateAsc(user, channelTitle,
                                    pageable);
                else
                    page = channelFeedItemUserRepository
                            .findByUserAndFeedItem_Channel_TitleAndFeedItem_publishLocalDateGreaterThanEqualOrderByFeedItem_PublishDateAsc(
                                    user, channelTitle, startDate, pageable);

                feedItemsUsers = page.getContent();
                feedItems = feedItemsUsers.stream().map((FeedItemUser e) -> e.getFeedItem())
                        .collect(Collectors.toList());
                break;
            } // end case

            case ORDER_BY_UNREAD: {

                break;
            } // end case

            default:
                return feedItems;
        }// end switch

        return feedItems;
    }
}

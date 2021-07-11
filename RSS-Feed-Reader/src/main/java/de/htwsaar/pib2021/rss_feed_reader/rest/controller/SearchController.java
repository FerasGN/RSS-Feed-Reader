package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import static de.htwsaar.pib2021.rss_feed_reader.constants.Constants.*;
import static de.htwsaar.pib2021.rss_feed_reader.constants.Endpoints.*;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.htwsaar.pib2021.rss_feed_reader.commands.FeedItemUserCommand;
import de.htwsaar.pib2021.rss_feed_reader.config.security.SecurityUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.FeedsService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.search.FeedSearchingService;

@Controller
public class SearchController {

    @Autowired
    private FeedsService feedsService;

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "currentFeedsUrl", required = false) String currentFeedsUrl,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "channelTitle", required = false) String channelTitle,
            @RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order,
            @RequestParam(value = "pageNumber", required = false) int pageNumber, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {

        if (q == null || q.trim().isEmpty())
            return "redirect:/feeds-page?currentFeedsUrl=" + currentFeedsUrl + "&channelTitle=" + channelTitle
                    + "&category=" + category + "&view=" + view + "&period=" + period + "&orderBy=" + order
                    + "&pageNumber=0";

        String filteredAndOrderedSearchResults = "";

        if (existViewAndPeriodAbdOrderParams(view, period, order)) { // if params exist use them
            filteredAndOrderedSearchResults = getSearchResultsPage(q, currentFeedsUrl, category, channelTitle, view,
                    period, order, pageNumber, model, securityUser.getUser());

        } else { // use default params
            filteredAndOrderedSearchResults = getSearchResultsPage(q, currentFeedsUrl, null, null, VIEW_CARDS,
                    PERIOD_ALL, ORDER_BY_LATEST, pageNumber, model, securityUser.getUser());
        }

        return filteredAndOrderedSearchResults;

    }

    private String getSearchResultsPage(String q, String currentFeedsUrl, String category, String channelTitle,
            String view, String period, String order, int pageNumber, Model model, User user) {

        String filteredAndOrderedSearchResults = "";
        if (VIEW_CARDS.equalsIgnoreCase(view))
            filteredAndOrderedSearchResults = getFilteredAndOrderedSearchResultsAsCards(q, user, model, currentFeedsUrl,
                    category, channelTitle, period, order, pageNumber);

        else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
            filteredAndOrderedSearchResults = getFilteredAndOrderedSearchResultsAsList(q, user, model, currentFeedsUrl,
                    category, channelTitle, period, order, pageNumber);

        return filteredAndOrderedSearchResults;

    }

    public String getFilteredAndOrderedSearchResultsAsCards(String q, User user, Model model, String currentFeedsUrl,
            String categoryName, String channelTitle, String period, String order, int pageNumber) {

        List<FeedItemUserCommand> feedItemUserCommands = getSerachResultsAsFeedItemCommands(q, user, currentFeedsUrl,
                categoryName, channelTitle, period, order, pageNumber);

        model.addAttribute("view", "cards");
        model.addAttribute("feeds", feedItemUserCommands);
        return "layouts/feeds-cards :: feeds-cards";
    }

    public String getFilteredAndOrderedSearchResultsAsList(String q, User user, Model model, String currentFeedsUrl,
            String categoryName, String channelTitle, String period, String order, int pageNumber) {

        List<FeedItemUserCommand> feedItemUserCommands = getSerachResultsAsFeedItemCommands(q, user, currentFeedsUrl,
                categoryName, channelTitle, period, order, pageNumber);

        model.addAttribute("view", "list");
        model.addAttribute("feeds", feedItemUserCommands);
        return "layouts/feeds-list :: feeds-list";
    }

    private List<FeedItemUserCommand> getSerachResultsAsFeedItemCommands(String q, User user, String currentFeedsUrl,
            String categoryName, String channelTitle, String period, String order, int pageNumber) {

        List<FeedItemUserCommand> feeds = Collections.emptyList();

        if (ALL_FEEDS_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = searchAllFeedItemCommands(q, user, period, order, pageNumber);

        else if (READ_LATER_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = searchReadLaterFeedItemCommands(q, user, period, order, pageNumber);

        else if (LIKED_FEEDS_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = searchLikedFeedItemCommands(q, user, period, order, pageNumber);

        else if (CATEGORY_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = searchCategoryFeedItemCommands(q, user, categoryName, period, order, pageNumber);

        else if (CHANNEL_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = searchChannelFeedItemCommands(q, user, channelTitle, period, order, pageNumber);

        return feeds;

    }

    private List<FeedItemUserCommand> searchAllFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.searchAllFeedItemCommands(q, user, period, order,
                pageNumber);
        return feedItemUserCommands;
    }

    private List<FeedItemUserCommand> searchReadLaterFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.searchReadLaterFeedItemCommands(q, user, period,
                order, pageNumber);

        return feedItemUserCommands;
    }

    private List<FeedItemUserCommand> searchLikedFeedItemCommands(String q, User user, String period, String order,
            int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.searchLikedFeedItemCommands(q, user, period,
                order, pageNumber);

        return feedItemUserCommands;
    }

    private List<FeedItemUserCommand> searchCategoryFeedItemCommands(String q, User user, String categoryName,
            String period, String order, int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.searchCategoryFeedItemCommands(q, user,
                categoryName, period, order, pageNumber);

        return feedItemUserCommands;
    }

    private List<FeedItemUserCommand> searchChannelFeedItemCommands(String q, User user, String channelTitle,
            String period, String order, int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.searchChannelFeedItemCommands(q, user,
                channelTitle, period, order, pageNumber);

        return feedItemUserCommands;
    }

    private boolean existViewAndPeriodAbdOrderParams(String view, String period, String order) {
        boolean correctView = VIEW_CARDS.equalsIgnoreCase(view) || VIEW_TITLE_ONLY.equalsIgnoreCase(view);
        boolean correctPeriod = PERIOD_ALL.equalsIgnoreCase(period) || PERIOD_LAST_SEVEN_DAYS.equalsIgnoreCase(period)
                || PERIOD_LAST_THIRTY_DAYS.equalsIgnoreCase(period) || PERIOD_TODAY.equalsIgnoreCase(period);
        boolean correctOrder = ORDER_BY_ALL_CATEGORIES.equalsIgnoreCase(order)
                || ORDER_BY_CATEGORY.equalsIgnoreCase(order) || ORDER_BY_CHANNEL.equalsIgnoreCase(order)
                || ORDER_BY_LATEST.equalsIgnoreCase(order) || ORDER_BY_OLDEST.equalsIgnoreCase(order)
                || ORDER_BY_UNREAD.equalsIgnoreCase(order) || ORDER_BY_MOST_RELEVANT.equalsIgnoreCase(order);
        return (correctView && correctPeriod && correctOrder);
    }

}
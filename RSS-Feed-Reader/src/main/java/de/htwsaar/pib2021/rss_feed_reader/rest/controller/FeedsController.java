package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import static de.htwsaar.pib2021.rss_feed_reader.constants.Constants.*;
import static de.htwsaar.pib2021.rss_feed_reader.constants.Endpoints.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import de.htwsaar.pib2021.rss_feed_reader.commands.ChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.commands.FeedItemCommand;
import de.htwsaar.pib2021.rss_feed_reader.config.security.SecurityUser;
import de.htwsaar.pib2021.rss_feed_reader.converters.ChannelUserToChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.ChannelService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.FeedsService;

@Controller
public class FeedsController {

    private ChannelService channelService;
    private FeedsService feedsService;

    public FeedsController(ChannelService channelService, FeedsService feedsService) {
        this.channelService = channelService;
        this.feedsService = feedsService;
    }

    /**
     * Returns a view that contains all feeds that match the two Criteria period and
     * order. If this endpoint was hit for the first time, the period and order will
     * be null, so the default values are period = all (all means published at any
     * time) and order = last (last means order feeds by the latest publication
     * date). Each time these creteria are changed, not the whole view is reloaded,
     * but only the view fragment containing the feeds.
     * 
     * @param period
     * @param order
     * @param model
     * @return String
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(ALL_FEEDS_URL)
    public String showAllFeeds(@RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {

        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);

        // if params exist use them
        if (existViewAndPeriodAbdOrderParams(view, period, order)) {
            String filteredAndOrderedFeeds = "";
            if (VIEW_CARDS.equalsIgnoreCase(view)) {
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser.getUser(), model,
                        ALL_FEEDS_URL, null, null, period, order, 0);
            } else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser.getUser(), model, ALL_FEEDS_URL,
                        null, null, period, order, 0);
            return filteredAndOrderedFeeds;
        } else {
            List<FeedItemCommand> feedItemCommands = new ArrayList<FeedItemCommand>();
            feedItemCommands = findAllFeedItemCommands(securityUser.getUser(), PERIOD_ALL, ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feedItemCommands);
        }
        return "all-feeds";
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/refresh-header")
    public String refreshSidePanel(Model model, @AuthenticationPrincipal SecurityUser securityUser) {
        model = initSidePanelFeedsInfo(model, securityUser);
        return "layouts/header :: header";
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/feeds-page")
    public String getFeedPage(@RequestParam(value = "currentFeedsUrl", required = false) String currentFeedsUrl,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "channelTitle", required = false) String channelTitle,
            @RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order,
            @RequestParam(value = "pageNumber", required = false) int pageNumber, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {

        String filteredAndOrderedFeeds = "";

        if (existViewAndPeriodAbdOrderParams(view, period, order)) { // if params exist use them
            filteredAndOrderedFeeds = getFeedPage(currentFeedsUrl, category, channelTitle, view, period, order,
                    pageNumber, model, securityUser.getUser());

        } else { // use default params
            filteredAndOrderedFeeds = getFeedPage(currentFeedsUrl, null, null, VIEW_CARDS, PERIOD_ALL, ORDER_BY_LATEST,
                    pageNumber, model, securityUser.getUser());
        }

        return filteredAndOrderedFeeds;
    }

    private String getFeedPage(String currentFeedsUrl, String category, String channelTitle, String view, String period,
            String order, int pageNumber, Model model, User user) {

        String filteredAndOrderedFeeds = "";
        if (VIEW_CARDS.equalsIgnoreCase(view))
            filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(user, model, currentFeedsUrl, category,
                    channelTitle, period, order, pageNumber);

        else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
            filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(user, model, currentFeedsUrl, category,
                    channelTitle, period, order, pageNumber);

        return filteredAndOrderedFeeds;
    }

    @GetMapping(READ_LATER_URL)
    public String showReadLaterFeeds(@RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {
        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);

        if (existViewAndPeriodAbdOrderParams(view, period, order)) {
            String filteredAndOrderedFeeds = "";
            if (VIEW_CARDS.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser.getUser(), model,
                        READ_LATER_URL, null, null, period, order, 0);
            else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser.getUser(), model,
                        READ_LATER_URL, null, null, period, order, 0);
            return filteredAndOrderedFeeds;
        } else {
            List<FeedItemCommand> feedItemCommands = new ArrayList<FeedItemCommand>();
            feedItemCommands = findReadLaterFeedItemCommands(securityUser.getUser(), PERIOD_ALL, ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feedItemCommands);
        }
        return "read-later";
    }

    @GetMapping(LIKED_FEEDS_URL)
    public String showLikedFeeds(@RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {
        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);

        if (existViewAndPeriodAbdOrderParams(view, period, order)) {
            String filteredAndOrderedFeeds = "";
            if (VIEW_CARDS.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser.getUser(), model,
                        LIKED_FEEDS_URL, null, null, period, order, 0);
            else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser.getUser(), model,
                        LIKED_FEEDS_URL, null, null, period, order, 0);
            return filteredAndOrderedFeeds;
        } else {
            List<FeedItemCommand> feedItemCommands = new ArrayList<FeedItemCommand>();
            feedItemCommands = findLikedFeedItemCommands(securityUser.getUser(), PERIOD_ALL, ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feedItemCommands);
        }
        return "read-later";
    }

    /**
     * @return List<FeedItem>
     */
    @GetMapping(CATEGORY_URL + "/{categoryName}")
    public String showAllFeedsofACategory(@PathVariable(value = "categoryName") String categoryName,
            @RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {

        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);

        if (existViewAndPeriodAbdOrderParams(view, period, order)) {
            String filteredAndOrderedFeeds = "";
            if (VIEW_CARDS.equalsIgnoreCase(view)) {
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser.getUser(), model, CATEGORY_URL,
                        categoryName, null, period, order, 0);
            } else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser.getUser(), model, CATEGORY_URL,
                        categoryName, null, period, order, 0);
            return filteredAndOrderedFeeds;
        } else {
            List<FeedItemCommand> feedItemCommands = new ArrayList<FeedItemCommand>();
            feedItemCommands = findCategoryFeedItemCommands(securityUser.getUser(), categoryName, PERIOD_ALL,
                    ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feedItemCommands);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("url", "/category");

        }
        return "all-feeds";
    }

    @GetMapping(CHANNEL_URL + "/{channelTitle}")
    public String showAllFeedsofAChannel(@PathVariable(value = "channelTitle") String channelTitle,
            @RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {

        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);

        // decode channel title because it is extracted from a url
        try {
            channelTitle = URLDecoder.decode(channelTitle, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (existViewAndPeriodAbdOrderParams(view, period, order)) {
            String filteredAndOrderedFeeds = "";
            if (VIEW_CARDS.equalsIgnoreCase(view)) {
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser.getUser(), model, CHANNEL_URL,
                        null, channelTitle, period, order, 0);
            } else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser.getUser(), model, CHANNEL_URL,
                        null, channelTitle, period, order, 0);
            return filteredAndOrderedFeeds;
        } else {
            List<FeedItemCommand> feedItemCommands = new ArrayList<FeedItemCommand>();
            feedItemCommands = findChannelFeedItemCommands(securityUser.getUser(), channelTitle, PERIOD_ALL,
                    ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feedItemCommands);
            model.addAttribute("channelTitle", channelTitle);
            model.addAttribute("url", "/channel");

        }
        return "all-feeds";
    }

    private boolean existViewAndPeriodAbdOrderParams(String view, String period, String order) {
        return (view != null && !view.trim().isEmpty()) && (order != null && !order.trim().isEmpty())
                && (period != null && !period.trim().isEmpty());
    }

    private Model initSidePanelFeedsInfo(Model model, SecurityUser securityUser) {
        User user = securityUser.getUser();
        List<String> categories = channelService.findAllChannelsCategoriesByUser(user);
        List<ChannelUser> channelUser = channelService.findAllChannelUserOrderedByCategory(user);
        ChannelUserToChannelCommand channelUserToChannelCommand = new ChannelUserToChannelCommand(channelService);
        List<ChannelCommand> channelCommands = channelUser.stream().map(cu -> channelUserToChannelCommand.convert(cu))
                .collect(Collectors.toList());
        model.addAttribute("channelCommand", new ChannelCommand());
        model.addAttribute("categories", categories);
        model.addAttribute("channelCommands", channelCommands);

        return model;
    }

    /**
     * @param model
     * @param period
     * @param order
     * @return String
     */
    public String getFilteredAndOrderedFeedsAsCards(User user, Model model, String currentFeedsUrl, String categoryName,
            String channelTitle, String period, String order, int pageNumber) {

        List<FeedItemCommand> feedItemCommands = getFeedItemCommands(user, currentFeedsUrl, categoryName, channelTitle,
                period, order, pageNumber);

        model.addAttribute("view", "cards");
        model.addAttribute("feeds", feedItemCommands);
        return "layouts/feeds-cards :: feeds-cards";
    }

    public String getFilteredAndOrderedFeedsAsList(User user, Model model, String currentFeedsUrl, String categoryName,
            String channelTitle, String period, String order, int pageNumber) {

        List<FeedItemCommand> feedItemCommands = getFeedItemCommands(user, currentFeedsUrl, categoryName, channelTitle,
                period, order, pageNumber);

        model.addAttribute("view", "list");
        model.addAttribute("feeds", feedItemCommands);
        return "layouts/feeds-list :: feeds-list";
    }

    private List<FeedItemCommand> getFeedItemCommands(User user, String currentFeedsUrl, String categoryName,
            String channelTitle, String period, String order, int pageNumber) {
        List<FeedItemCommand> feeds = Collections.emptyList();

        if (ALL_FEEDS_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findAllFeedItemCommands(user, period, order, pageNumber);

        else if (READ_LATER_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findReadLaterFeedItemCommands(user, period, order, pageNumber);

        else if (LIKED_FEEDS_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findLikedFeedItemCommands(user, period, order, pageNumber);

        else if (CATEGORY_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findCategoryFeedItemCommands(user, categoryName, period, order, pageNumber);

        else if (CHANNEL_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findChannelFeedItemCommands(user, channelTitle, period, order, pageNumber);

        return feeds;
    }

    private List<FeedItemCommand> findAllFeedItemCommands(User user, String period, String order, int pageNumber) {
        List<FeedItemCommand> feedItemCommands = feedsService.findAllFeedItemsCommands(user, period, order, pageNumber);
        return feedItemCommands;
    }

    private List<FeedItemCommand> findReadLaterFeedItemCommands(User user, String period, String order,
            int pageNumber) {
        List<FeedItemCommand> feedItemCommands = feedsService.findReadLaterFeedItemsCommands(user, period, order,
                pageNumber);

        return feedItemCommands;
    }

    private List<FeedItemCommand> findLikedFeedItemCommands(User user, String period, String order, int pageNumber) {
        List<FeedItemCommand> feedItemCommands = feedsService.findLikedFeedItemsCommands(user, period, order,
                pageNumber);

        return feedItemCommands;
    }

    private List<FeedItemCommand> findCategoryFeedItemCommands(User user, String categoryName, String period,
            String order, int pageNumber) {
        List<FeedItemCommand> feedItemCommands = feedsService.findCategoryFeedItemCommands(user, categoryName, period,
                order, pageNumber);

        return feedItemCommands;
    }

    private List<FeedItemCommand> findChannelFeedItemCommands(User user, String channelTitle, String period,
            String order, int pageNumber) {
        List<FeedItemCommand> feedItemCommands = feedsService.findChannelFeedItemCommands(user, channelTitle, period,
                order, pageNumber);

        return feedItemCommands;
    }

    /**
     * @param period
     * @param order
     * @return boolean
     */

    // private List<FeedItemCommand>
    // getFeedsItemCommandsOfChannelCategoryFilteredAndOrdered(User user,
    // String categoryName, String period, String order, int pageNumber) {
    // List<FeedItemCommand> feedItemCommands =
    // feedsService.findAllFeedItemsCommands(user, categoryName, period,
    // order, pageNumber);
    // return feedItemCommands;
    // }

}

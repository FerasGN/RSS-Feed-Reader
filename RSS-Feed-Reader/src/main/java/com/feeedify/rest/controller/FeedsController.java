package com.feeedify.rest.controller;

import static com.feeedify.constants.Constants.*;
import static com.feeedify.constants.Endpoints.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.feeedify.commands.CategoryCommand;
import com.feeedify.commands.ChannelCommand;
import com.feeedify.commands.FeedItemUserCommand;
import com.feeedify.commands.LikeCommand;
import com.feeedify.commands.ReadCommand;
import com.feeedify.commands.ReadLaterCommand;
import com.feeedify.config.security.SecurityUser;
import com.feeedify.converters.CategoryToCategoryCommand;
import com.feeedify.converters.ChannelUserToChannelCommand;
import com.feeedify.database.entity.Category;
import com.feeedify.database.entity.ChannelUser;
import com.feeedify.database.entity.User;
import com.feeedify.rest.service.ChannelService;
import com.feeedify.rest.service.FeedsService;

@Controller
public class FeedsController {

    private ChannelService channelService;
    private FeedsService feedsService;

    public FeedsController(ChannelService channelService, FeedsService feedsService) {
        this.channelService = channelService;
        this.feedsService = feedsService;
    }

    /**
     * Returns a view that contains all feeds that match the THREE Criteria view,
     * period and order. If this endpoint was hit for the first time, the view,
     * period and order will be null, so the default values are view = cards, period
     * = all (all means published at any time) and order = last (last means order
     * feeds by the latest publication date). Each time these creteria are changed,
     * not the whole view is reloaded, but only the view fragment containing the
     * feeds.
     * 
     * @param period
     * @param order
     * @param model
     * @return String view name
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
            List<FeedItemUserCommand> feedItemUserCommands = new ArrayList<FeedItemUserCommand>();
            feedItemUserCommands = findAllFeedItemUserCommands(securityUser.getUser(), PERIOD_ALL, ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("allFeedsUrl", ALL_FEEDS_URL);
            model.addAttribute("feeds", feedItemUserCommands);
        }
        return "all-feeds";
    }

    /**
     * Refreshs the side panel.
     * 
     * @param model
     * @param securityUser
     * @return String
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(REFRESH_HEADER_URL)
    public String refreshSidePanel(Model model, @AuthenticationPrincipal SecurityUser securityUser) {
        model = initSidePanelFeedsInfo(model, securityUser);
        return "layouts/header :: header";
    }

    /**
     * It is used to perform ajax call to get a page of feed items.
     * 
     * @return String
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(FEEDS_PAGE_URL)
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

    /**
     * @param currentFeedsUrl
     * @param category
     * @param channelTitle
     * @param view
     * @param period
     * @param order
     * @param pageNumber
     * @param model
     * @param user
     * @return String
     */
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

    /**
     * Shows only the read later feeds of a specificuser.
     * 
     * @return String view name
     */
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
            List<FeedItemUserCommand> feedItemUserCommands = new ArrayList<FeedItemUserCommand>();
            feedItemUserCommands = findReadLaterFeedItemUserCommands(securityUser.getUser(), PERIOD_ALL,
                    ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("readLaterUrl", READ_LATER_URL);
            model.addAttribute("feeds", feedItemUserCommands);
        }
        return "read-later";
    }

    /**
     * Shows only the liked feeds of a specific user.
     * 
     * @return String view name
     */
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
            List<FeedItemUserCommand> feedItemUserCommands = new ArrayList<FeedItemUserCommand>();
            feedItemUserCommands = findLikedFeedItemUserCommands(securityUser.getUser(), PERIOD_ALL, ORDER_BY_LATEST,
                    0);
            model.addAttribute("view", "cards");
            model.addAttribute("likedFeedsUrl", LIKED_FEEDS_URL);
            model.addAttribute("feeds", feedItemUserCommands);
        }
        return "liked-feeds";
    }

    /**
     * Shows only the feeds of a specefic category of a specific user.
     * 
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
            List<FeedItemUserCommand> feedItemUserCommands = new ArrayList<FeedItemUserCommand>();
            feedItemUserCommands = findCategoryFeedItemCommands(securityUser.getUser(), categoryName, PERIOD_ALL,
                    ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feedItemUserCommands);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("orderSelectUrl", "/category");
            model.addAttribute("categoryUrl", CATEGORY_URL + "/" + categoryName);

        }
        return "all-feeds";
    }

    /**
     * Shows only the feeds of a specefic channel of a specific user.
     * 
     * @return String
     */
    @GetMapping(CATEGORY_URL + "/{categoryName}" + CHANNEL_URL + "/{channelTitle}")
    public String showAllFeedsofAChannel(@PathVariable(value = "categoryName") String categoryName,
            @PathVariable(value = "channelTitle") String channelTitle,
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
            List<FeedItemUserCommand> feedItemUserCommands = new ArrayList<FeedItemUserCommand>();
            feedItemUserCommands = findChannelFeedItemCommands(securityUser.getUser(), channelTitle, PERIOD_ALL,
                    ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feedItemUserCommands);
            model.addAttribute("channelTitle", channelTitle);
            model.addAttribute("orderSelectUrl", "/channel");
            model.addAttribute("categoryUrl", CATEGORY_URL + "/" + categoryName);
            model.addAttribute("channelUrl", CHANNEL_URL + "/" + channelTitle);

        }
        return "all-feeds";
    }

    /**
     * Like a feed item.
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = { LIKE_URL }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody void like(@RequestBody LikeCommand likeCommand,
            @AuthenticationPrincipal SecurityUser securityUser) {

        boolean liked = likeCommand.isLiked();
        Long feedItemId = likeCommand.getFeedId();

        feedsService.changeLikeStatus(securityUser.getId(), feedItemId, liked);
        feedsService.incrementClicksNumber(securityUser.getId(), feedItemId);

    }

    /**
     * Read feed Item later.
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = { MARK_AS_READ_LATER_URL }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody void markAsReadLater(@RequestBody ReadLaterCommand readLaterCommand,
            @AuthenticationPrincipal SecurityUser securityUser) {

        boolean readLater = readLaterCommand.isReadLater();
        Long feedItemId = readLaterCommand.getFeedId();

        feedsService.changeReadLaterStatus(securityUser.getId(), feedItemId, readLater);
        feedsService.incrementClicksNumber(securityUser.getId(), feedItemId);

    }

    /**
     * Mark feed item as already read.
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = { MARK_AS_READ_URL }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody void markAsRead(@RequestBody ReadCommand readCommand,
            @AuthenticationPrincipal SecurityUser securityUser) {

        boolean read = readCommand.isRead();
        Long feedItemId = readCommand.getFeedId();

        feedsService.changeReadStatus(securityUser.getId(), feedItemId, read);
        feedsService.incrementClicksNumber(securityUser.getId(), feedItemId);

    }

    /**
     * Checks whether the specified criteria exist.
     * 
     * @param view
     * @param period
     * @param order
     * @return boolean
     */
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

    /**
     * Initializes the side panel.
     * 
     * @param model
     * @param securityUser
     * @return Model
     */
    public Model initSidePanelFeedsInfo(Model model, SecurityUser securityUser) {
        User user = securityUser.getUser();

        List<CategoryCommand> categoryCommands = findCategoryCommands(user);
        List<ChannelUser> channelsUser = channelService.findAllChannelUserOrderedByCategory(user);
        ChannelUserToChannelCommand channelUserToChannelCommand = new ChannelUserToChannelCommand(feedsService);
        List<ChannelCommand> channelCommands = channelsUser.stream().map(cu -> channelUserToChannelCommand.convert(cu))
                .collect(Collectors.toList());
        model.addAttribute("numberOfUnreadFeeds", feedsService.findNumberOfUnreadFeeds(user));
        model.addAttribute("channelCommand", new ChannelCommand());
        model.addAttribute("categoryCommands", categoryCommands);
        model.addAttribute("channelCommands", channelCommands);

        return model;
    }

    /**
     * 
     * Returns feed items as cards.
     * 
     * @param user
     * @param model
     * @param currentFeedsUrl
     * @param categoryName
     * @param channelTitle
     * @param period
     * @param order
     * @param pageNumber
     * @return String
     */
    public String getFilteredAndOrderedFeedsAsCards(User user, Model model, String currentFeedsUrl, String categoryName,
            String channelTitle, String period, String order, int pageNumber) {

        List<FeedItemUserCommand> feedItemUserCommands = getFeedItemCommands(user, currentFeedsUrl, categoryName,
                channelTitle, period, order, pageNumber);

        model.addAttribute("view", "cards");
        model.addAttribute("feeds", feedItemUserCommands);
        return "layouts/feeds-cards :: feeds-cards";
    }

    /**
     * Returns feed items as list.
     * 
     * @param user
     * @param model
     * @param currentFeedsUrl
     * @param categoryName
     * @param channelTitle
     * @param period
     * @param order
     * @param pageNumber
     * @return String
     */
    public String getFilteredAndOrderedFeedsAsList(User user, Model model, String currentFeedsUrl, String categoryName,
            String channelTitle, String period, String order, int pageNumber) {

        List<FeedItemUserCommand> feedItemUserCommands = getFeedItemCommands(user, currentFeedsUrl, categoryName,
                channelTitle, period, order, pageNumber);

        model.addAttribute("view", "list");
        model.addAttribute("feeds", feedItemUserCommands);
        return "layouts/feeds-list :: feeds-list";
    }

    /**
     * @param user
     * @param currentFeedsUrl
     * @param categoryName
     * @param channelTitle
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    private List<FeedItemUserCommand> getFeedItemCommands(User user, String currentFeedsUrl, String categoryName,
            String channelTitle, String period, String order, int pageNumber) {
        List<FeedItemUserCommand> feeds = Collections.emptyList();

        if (ALL_FEEDS_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findAllFeedItemUserCommands(user, period, order, pageNumber);

        else if (READ_LATER_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findReadLaterFeedItemUserCommands(user, period, order, pageNumber);

        else if (LIKED_FEEDS_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findLikedFeedItemUserCommands(user, period, order, pageNumber);

        else if (CATEGORY_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findCategoryFeedItemCommands(user, categoryName, period, order, pageNumber);

        else if (CHANNEL_URL.equalsIgnoreCase(currentFeedsUrl))
            feeds = findChannelFeedItemCommands(user, channelTitle, period, order, pageNumber);

        return feeds;
    }

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    private List<FeedItemUserCommand> findAllFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.findAllFeedItemUserCommands(user, period, order,
                pageNumber);
        return feedItemUserCommands;
    }

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    private List<FeedItemUserCommand> findReadLaterFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.findReadLaterFeedItemUserCommands(user, period,
                order, pageNumber);

        return feedItemUserCommands;
    }

    /**
     * @param user
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    private List<FeedItemUserCommand> findLikedFeedItemUserCommands(User user, String period, String order,
            int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.findLikedFeedItemUserCommands(user, period, order,
                pageNumber);

        return feedItemUserCommands;
    }

    /**
     * @param user
     * @param categoryName
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    private List<FeedItemUserCommand> findCategoryFeedItemCommands(User user, String categoryName, String period,
            String order, int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.findCategoryFeedItemUserCommands(user,
                categoryName, period, order, pageNumber);

        return feedItemUserCommands;
    }

    /**
     * @param user
     * @param channelTitle
     * @param period
     * @param order
     * @param pageNumber
     * @return List<FeedItemUserCommand>
     */
    private List<FeedItemUserCommand> findChannelFeedItemCommands(User user, String channelTitle, String period,
            String order, int pageNumber) {
        List<FeedItemUserCommand> feedItemUserCommands = feedsService.findChannelFeedItemUserCommands(user,
                channelTitle, period, order, pageNumber);

        return feedItemUserCommands;
    }

    /**
     * @param user
     * @return List<CategoryCommand>
     */
    public List<CategoryCommand> findCategoryCommands(User user) {
        List<String> categories = channelService.findAllChannelsCategoriesByUser(user);
        List<CategoryCommand> categoryCommands = categories.stream().map(categroyName -> {
            CategoryToCategoryCommand categoryToCategoryCommand = new CategoryToCategoryCommand(feedsService);
            categoryToCategoryCommand.setUser(user);
            return categoryToCategoryCommand.convert(new Category(categroyName));
        }).collect(Collectors.toList());

        return categoryCommands;
    }

}

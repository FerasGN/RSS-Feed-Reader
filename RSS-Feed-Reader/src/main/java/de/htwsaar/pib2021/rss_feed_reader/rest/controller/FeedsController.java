package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import static de.htwsaar.pib2021.rss_feed_reader.constants.Constants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import de.htwsaar.pib2021.rss_feed_reader.commands.ChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.commands.FeedItemCommand;
import de.htwsaar.pib2021.rss_feed_reader.config.security.SecurityUser;
import de.htwsaar.pib2021.rss_feed_reader.converters.ChannelUserToChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.converters.FeedItemToFeedItemCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Category;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.FeedItem;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Sse.SseNotification;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.ChannelService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.FeedsService;

@Controller
public class FeedsController {

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

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
    @GetMapping("/all-feeds")
    public String showAllFeeds(@RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {

        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);

        if (existVieAndPeriodAbdOrderParams(view, period, order)) {
            String filteredAndOrderedFeeds = "";
            if (VIEW_CARDS.equalsIgnoreCase(view)) {
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser, model,
                        ORDER_BY_ALL_CATEGORIES, period, order, 0);
            } else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser, model, ORDER_BY_ALL_CATEGORIES,
                        period, order, 0);
            return filteredAndOrderedFeeds;
        } else {
            List<FeedItemCommand> feeds = new ArrayList<FeedItemCommand>();
            feeds = getFilteredAndOrderedFeeds(securityUser, "all", "latest", 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feeds);
        }
        return "all-feeds";
    }

    private Model initSidePanelFeedsInfo(Model model, SecurityUser securityUser) {
        List<String> categories = channelService.findAllChannelsCategoriesByUser(securityUser.getUser());
        List<ChannelUser> channelUser = channelService.findAllChannelUserOrderedByCategory();
        ChannelUserToChannelCommand channelUserToChannelCommand = new ChannelUserToChannelCommand(channelService);
        List<ChannelCommand> channelCommands = channelUser.stream().map(cu -> channelUserToChannelCommand.convert(cu))
                .collect(Collectors.toList());

        model.addAttribute("channelCommand", new ChannelCommand());
        model.addAttribute("categories", categories);
        model.addAttribute("channelCommands", channelCommands);

        return model;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/feeds-page")
    public String getFeedPage(@RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order,
            @RequestParam(value = "pageNumber", required = false) int pageNumber, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {

        String filteredAndOrderedFeeds = "";

        if (existVieAndPeriodAbdOrderParams(view, period, order)) {
            if (VIEW_CARDS.equalsIgnoreCase(view))
                if (category == null)
                    filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser, model,
                            ORDER_BY_ALL_CATEGORIES, period, order, pageNumber);
                else
                    filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser, model, category, period,
                            order, pageNumber);
            else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
                if (category == null)
                    filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser, model,
                            ORDER_BY_ALL_CATEGORIES, period, order, pageNumber);
                else
                    filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser, model, category, period,
                            order, pageNumber);
        } else {
            filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser, model, ORDER_BY_ALL_CATEGORIES,
                    PERIOD_ALL, ORDER_BY_LATEST, pageNumber);
        }

        return filteredAndOrderedFeeds;
    }

    /**
     * @param model
     * @param period
     * @param order
     * @return String
     */
    public String getFilteredAndOrderedFeedsAsCards(SecurityUser securityUser, Model model, String categoryName,
            String period, String order, int pageNumber) {
        List<FeedItemCommand> feeds = new ArrayList<FeedItemCommand>();
        if (ORDER_BY_ALL_CATEGORIES.equals(categoryName) && ORDER_BY_CATEGORY.equals(order))
            feeds = getFeedsOfChannelCategoryFilteredAndOrderedFeeds(securityUser, categoryName, period, order,
                    pageNumber);
        else if (ORDER_BY_ALL_CATEGORIES.equals(categoryName))
            feeds = getFilteredAndOrderedFeeds(securityUser, period, order, pageNumber);
        else
            feeds = getFeedsOfChannelCategoryFilteredAndOrderedFeeds(securityUser, categoryName, period, order,
                    pageNumber);

        model.addAttribute("view", "cards");
        model.addAttribute("feeds", feeds);
        return "layouts/feeds-cards :: feeds-cards";
    }

    public String getFilteredAndOrderedFeedsAsList(SecurityUser securityUser, Model model, String categoryName,
            String period, String order, int pageNumber) {
        List<FeedItemCommand> feeds = new ArrayList<FeedItemCommand>();
        if (ORDER_BY_ALL_CATEGORIES.equals(categoryName) && ORDER_BY_CATEGORY.equals(order))
            feeds = getFeedsOfChannelCategoryFilteredAndOrderedFeeds(securityUser, categoryName, period, order,
                    pageNumber);
        else if (ORDER_BY_ALL_CATEGORIES.equals(categoryName))
            feeds = getFilteredAndOrderedFeeds(securityUser, period, order, pageNumber);
        else
            feeds = getFeedsOfChannelCategoryFilteredAndOrderedFeeds(securityUser, categoryName, period, order,
                    pageNumber);
        model.addAttribute("view", "list");
        model.addAttribute("feeds", feeds);
        return "layouts/feeds-list :: feeds-list";
    }

    /**
     * @param period
     * @param order
     * @return boolean
     */
    private boolean existVieAndPeriodAbdOrderParams(String view, String period, String order) {
        return (view != null && !view.trim().isEmpty()) && (order != null && !order.trim().isEmpty())
                && (period != null && !period.trim().isEmpty());
    }

    @GetMapping("/read-later")
    public String showReadLaterFeeds(@RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {
        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);

        if (existVieAndPeriodAbdOrderParams(view, period, order)) {
            String filteredAndOrderedFeeds = "";
            if (VIEW_CARDS.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser, model,
                        ORDER_BY_ALL_CATEGORIES, period, order, 0);
            else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser, model, ORDER_BY_ALL_CATEGORIES,
                        period, order, 0);
            return filteredAndOrderedFeeds;
        } else {
            List<FeedItemCommand> feeds = new ArrayList<FeedItemCommand>();
            feeds = getFilteredAndOrderedFeeds(securityUser, "all", "latest", 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feeds);
        }
        return "read-later";
    }

    @GetMapping("/liked-feeds")
    public String showLikedFeeds(@RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {
        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);

        if (existVieAndPeriodAbdOrderParams(view, period, order)) {
            String filteredAndOrderedFeeds = "";
            if ("cards".equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser, model,
                        ORDER_BY_ALL_CATEGORIES, period, order, 0);
            else if ("title-only".equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser, model, ORDER_BY_ALL_CATEGORIES,
                        period, order, 0);
            return filteredAndOrderedFeeds;
        } else {
            List<FeedItemCommand> feeds = new ArrayList<FeedItemCommand>();
            feeds = getFilteredAndOrderedFeeds(securityUser, "all", "latest", 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feeds);
        }
        return "liked-feeds";
    }

    /**
     * @return List<FeedItem>
     */
    @GetMapping("/category/{categoryName}")
    public String showAllFeedsofACategory(@PathVariable(value = "categoryName") String categoryName,
            @RequestParam(value = "view", required = false) String view,
            @RequestParam(value = "period", required = false) String period,
            @RequestParam(value = "orderBy", required = false) String order, Model model,
            @AuthenticationPrincipal SecurityUser securityUser) {

        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);

        if (existVieAndPeriodAbdOrderParams(view, period, order)) {
            String filteredAndOrderedFeeds = "";
            if (VIEW_CARDS.equalsIgnoreCase(view)) {
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsCards(securityUser, model, categoryName, period,
                        order, 0);
            } else if (VIEW_TITLE_ONLY.equalsIgnoreCase(view))
                filteredAndOrderedFeeds = getFilteredAndOrderedFeedsAsList(securityUser, model, categoryName, period,
                        order, 0);
            return filteredAndOrderedFeeds;
        } else {
            List<FeedItemCommand> feeds = new ArrayList<FeedItemCommand>();
            feeds = getFeedsOfChannelCategoryFilteredAndOrderedFeeds(securityUser, categoryName, PERIOD_ALL,
                    ORDER_BY_LATEST, 0);
            model.addAttribute("view", "cards");
            model.addAttribute("feeds", feeds);
            model.addAttribute("categoryName", categoryName);
            model.addAttribute("url", "/category");

        }
        return "all-feeds";
    }

    // @GetMapping("/channel/{channelName}")
    // public String showAllFeedsofAChannel(@PathVariable(value = "channelName")
    // String channelName,
    // @RequestParam(value = "orderBy", required = false) String order, Model model)
    // {
    // List<FeedItem> feeds = new ArrayList<FeedItem>();
    // if (channelName != null && !channelName.trim().isEmpty()) {
    // if (order != null && !order.trim().isEmpty()) {
    // feeds = getAllFeedsOrderedBy(order);
    // model.addAttribute("feeds", feeds);
    // return "layouts/feeds-cards :: feeds-cards";
    // } else {
    // feeds = getAllFeedsOrderedBy("latest");
    // model.addAttribute("feeds", feeds);
    // }
    // }

    // return "all-feeds";
    // }

    private List<FeedItemCommand> getFilteredAndOrderedFeeds(SecurityUser securityUser, String period, String order,
            int pageNumber) {
        List<FeedItemCommand> feedItemCommands = new ArrayList<>();
        List<FeedItem> feedItems = feedsService.findAllFeeds(securityUser.getUser(), period, order, pageNumber);
        feedItems.stream().forEach(f -> {
            // convert feedItem to feedItemCommand
            FeedItemToFeedItemCommand feedItemToFeedItemCommand = new FeedItemToFeedItemCommand();
            Category category = channelService.findChannelCategory(securityUser.getUser(), f.getChannel());
            feedItemToFeedItemCommand.setUser(securityUser.getUser());
            feedItemToFeedItemCommand.setChannelCategory(category.getName());
            feedItemCommands.add(feedItemToFeedItemCommand.convert(f));
        });

        return feedItemCommands;
    }

    private List<FeedItemCommand> getFeedsOfChannelCategoryFilteredAndOrderedFeeds(SecurityUser securityUser,
            String categoryName, String period, String order, int pageNumber) {
        List<FeedItemCommand> feedItemCommands = new ArrayList<>();
        List<FeedItem> feedItems = feedsService.findAllFeedsByChannelCategory(securityUser.getUser(), categoryName,
                period, order, pageNumber);
        feedItems.stream().forEach(f -> {
            // convert feedItem to feedItemCommand
            FeedItemToFeedItemCommand feedItemToFeedItemCommand = new FeedItemToFeedItemCommand();
            Category category = channelService.findChannelCategory(securityUser.getUser(), f.getChannel());
            feedItemToFeedItemCommand.setUser(securityUser.getUser());
            feedItemToFeedItemCommand.setChannelCategory(category.getName());
            feedItemCommands.add(feedItemToFeedItemCommand.convert(f));
        });

        return feedItemCommands;
    }

    /**
     * 
     * @param securityUser
     * @return SseEmitter
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/sse-notifications")
    public SseEmitter getSseNotification(@AuthenticationPrincipal SecurityUser securityUser) {
        SseEmitter emitter = new SseEmitter();

        this.emitters.put(securityUser.getUsername(), emitter);

        emitter.onCompletion(() -> this.emitters.remove(securityUser.getUsername()));
        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(securityUser.getUsername());
        });
        return emitter;
    }

    /**
     * Listened to emerging sent server event notifications.
     * 
     * 
     * @param sseNotification
     */
    @EventListener
    public void onSseNotification(SseNotification sseNotification) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.emitters.forEach((username, emitter) -> {
            try {
                // send the notification to its associated user only
                if (username.equalsIgnoreCase(sseNotification.getUsername()))
                    emitter.send(sseNotification);
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        this.emitters.remove(deadEmitters);
    }

}

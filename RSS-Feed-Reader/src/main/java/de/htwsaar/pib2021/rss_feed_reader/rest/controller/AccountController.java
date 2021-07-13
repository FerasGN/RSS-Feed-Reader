package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import de.htwsaar.pib2021.rss_feed_reader.commands.CategoryCommand;
import de.htwsaar.pib2021.rss_feed_reader.commands.ChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.commands.UserProfileUpdateCommand;
import de.htwsaar.pib2021.rss_feed_reader.config.security.SecurityUser;
import de.htwsaar.pib2021.rss_feed_reader.converters.CategoryToCategoryCommand;
import de.htwsaar.pib2021.rss_feed_reader.converters.ChannelUserToChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Category;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.ChannelUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.AccountService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.ChannelService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.FeedsService;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private FeedsService feedsService;
    @Autowired
    private ChannelService channelService;

    @PostMapping(value = { "/save-interests" }, consumes = "application/json")
    public ModelAndView saveUserInterests(@RequestBody List<String> interests, ModelAndView mav,
            @AuthenticationPrincipal SecurityUser securityUser) {

        accountService.saveUserInterests(securityUser.getUser(), interests);

        mav.setViewName("redirect:/all-feeds");
        return mav;
    }

    @GetMapping(value = { "/account" })
    public String getAccount(Model model, @AuthenticationPrincipal SecurityUser securityUser) {

        // add categories, channels and the number of unread feeds
        model = initSidePanelFeedsInfo(model, securityUser);
        UserProfileUpdateCommand userProfileUpdateCommand = accountService
                .convertToUserProfileUpdateCommand(securityUser.getUser());

        model.addAttribute("userProfileUpdateCommand", userProfileUpdateCommand);

        return "account";
    }

    private Model initSidePanelFeedsInfo(Model model, SecurityUser securityUser) {
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

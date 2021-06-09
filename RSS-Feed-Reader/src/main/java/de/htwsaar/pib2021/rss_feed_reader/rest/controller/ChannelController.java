package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import java.util.Optional;

import com.rometools.rome.io.FeedException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.NotValidURLException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.htwsaar.pib2021.rss_feed_reader.commands.ChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.config.security.SecurityUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.ChannelService;

@Controller
public class ChannelController {

    private ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    /**
     * @param mav
     * @param url
     * @return ModelAndView
     */
    @GetMapping(value = { "/search-channel" })
    public ModelAndView searchForChannel(ModelAndView mav, @RequestParam("url") String url,
            @AuthenticationPrincipal SecurityUser securityUser) {

        boolean correctUrl = channelService.isRssURLCorrect(url);
        boolean channelExists = channelService.existsChannelURL(securityUser.getUser(), url);

        if (!correctUrl) {
            mav.addObject("channelInfo", "URL was not found");

        } else if (channelExists) {
            mav.addObject("channelInfo", "Channel already exists");

        } else {
            mav.addObject("channelInfo", "Name of the channel");
            mav.addObject("channelImage",
                    "//storage.googleapis.com/site-assets/UteRlEWI7AuSWrnzo0700y72vv9UnxO7o4qDzhto5xA_icon-16f89735391");
        }

        ChannelCommand channelCommand = new ChannelCommand();
        channelCommand.setUrl(url);

        mav.addObject("channelCommand", channelCommand);
        mav.setViewName("layouts/subscribe-modal :: subscribe-modal");
        return mav;
    }

    /**
     * @param mav
     * @param channelCommand
     * @return ModelAndView
     */
    @PostMapping(value = { "/save-channel" })
    public ModelAndView saveChannel(ModelAndView mav, ChannelCommand channelCommand, @AuthenticationPrincipal SecurityUser securityUser) {
        System.out.println("Channel = " + channelCommand.getUrl());
        String url = channelCommand.getUrl();
        String category = channelCommand.getCategory();

        try {
            Optional<Channel> channel = channelService.subscribeToChannel(securityUser.getUser(), url, "Tech");
        } catch (Exception e) {

        }
        mav.setViewName("redirect:/all-feeds");
        return mav;
    }
}

package com.feeedify.rest.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rometools.rome.feed.synd.SyndFeed;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.feeedify.commands.ChannelCommand;
import com.feeedify.commands.PostChannelCommand;
import com.feeedify.config.security.SecurityUser;
import com.feeedify.database.entity.Channel;
import com.feeedify.rest.service.ChannelService;
import com.feeedify.rest.service.FaviconExtractorService;

@Controller
public class ChannelController {

    private ChannelService channelService;
    private FaviconExtractorService faviconExtractorService;

    public ChannelController(ChannelService channelService, FaviconExtractorService faviconExtractorService) {
        this.channelService = channelService;
        this.faviconExtractorService = faviconExtractorService;
    }

    /**
     * @param mav
     * @param url
     * @return ModelAndView
     */
    @GetMapping(value = { "/search-channel" })
    public ModelAndView searchForChannel(ModelAndView mav, @RequestParam("url") String url,
            @AuthenticationPrincipal SecurityUser securityUser) {

        Optional<SyndFeed> parsedRssFeedFromURL = channelService.parseRssFeedFromURL(url);
        boolean channelExists = channelService.existsChannelURL(securityUser.getUser(), url);

        if (!parsedRssFeedFromURL.isPresent()) {
            mav.addObject("channelInfo", "URL was not found");

        } else if (channelExists) {
            mav.addObject("channelInfo", "Channel already exists");

        } else {
            SyndFeed feed = parsedRssFeedFromURL.get();
            mav.addObject("channelInfo", feed.getTitle());

            try {
                List<URL> links = faviconExtractorService.findFaviconLinks(feed.getLink());
                if (!links.isEmpty())
                    mav.addObject("faviconLink", links.get(0).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        ChannelCommand channelCommand = new ChannelCommand();
        channelCommand.setChannelUrl(url);

        mav.addObject("channelCommand", channelCommand);
        mav.setViewName("layouts/subscribe-modal :: subscribe-modal");
        return mav;
    }

    /**
     * @param mav
     * @param channelCommand
     * @return ModelAndView
     */
    @PostMapping(value = { "/save-channel" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ModelAndView saveChannel(ModelAndView mav, @RequestBody PostChannelCommand postChannelCommand,
            @AuthenticationPrincipal SecurityUser securityUser) {
        String url = postChannelCommand.getChannelUrl();
        String categoryName = postChannelCommand.getCategory();
        try {
            Optional<Channel> channel = channelService.subscribeToChannel(securityUser.getUser(), url, categoryName);

        } catch (Exception e) {

        }

        if (postChannelCommand.getCategoryUrl() != null)
            mav.setViewName("redirect:/feeds-page?currentFeedsUrl=" + postChannelCommand.getCurrentFeedsUrl()
                    + "&category=" + postChannelCommand.getCategory() + "&view=" + postChannelCommand.getSelectedView()
                    + "&period=" + postChannelCommand.getSelectedPeriod() + "&orderBy="
                    + postChannelCommand.getSelectedOrder() + "&pageNumber=0");
        else if (postChannelCommand.getChannelUrl() != null)
            mav.setViewName("redirect:/feeds-page?currentFeedsUrl=" + postChannelCommand.getCurrentFeedsUrl()
                    + "&channelTitle=" + postChannelCommand.getChannelTitle() + "&view="
                    + postChannelCommand.getSelectedView() + "&period=" + postChannelCommand.getSelectedPeriod()
                    + "&orderBy=" + postChannelCommand.getSelectedOrder() + "&pageNumber=0");
        else
            mav.setViewName("redirect:/feeds-page?currentFeedsUrl=" + postChannelCommand.getCurrentFeedsUrl() + "&view="
                    + postChannelCommand.getSelectedView() + "&period=" + postChannelCommand.getSelectedPeriod()
                    + "&orderBy=" + postChannelCommand.getSelectedOrder() + "&pageNumber=0");

        return mav;
    }

    @GetMapping(value = "/find-categories")
    @ResponseBody
    public List<String> findCategories(@AuthenticationPrincipal SecurityUser securityUser) {
        List<String> allCategories = new ArrayList<String>();
        try {

            allCategories = channelService.findAllChannelsCategoriesByUser(securityUser.getUser());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return allCategories;

    }

}

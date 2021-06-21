package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rometools.rome.feed.synd.SyndFeed;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.htwsaar.pib2021.rss_feed_reader.commands.ChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.config.security.SecurityUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.Channel;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.ChannelService;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.FaviconExtractorService;

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
    @PostMapping(value = { "/save-channel" }, consumes = "application/json")
    public ModelAndView saveChannel(ModelAndView mav, @RequestBody List<String[]> data,
            @AuthenticationPrincipal SecurityUser securityUser) {
        String url = data.get(0)[0];
        String categoryName = data.get(0)[1];

        try {
            Optional<Channel> channel = channelService.subscribeToChannel(securityUser.getUser(), url, categoryName);

        } catch (Exception e) {

        }
        mav.setViewName("redirect:/feeds-page?view=" + data.get(1)[0] + "&period=" + data.get(1)[1] + "&orderBy="
                + data.get(1)[2] + "&pageNumber=0");
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

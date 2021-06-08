package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import com.rometools.rome.io.FeedException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.NotValidURLException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.htwsaar.pib2021.rss_feed_reader.commands.ChannelCommand;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.ChannelService;

import java.io.IOException;

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
    public ModelAndView searchForChannel(ModelAndView mav, @RequestParam("url") String url)
            throws NotValidURLException, IOException, FeedException, Exception {

        boolean correctUrl = channelService.isRssURLPresent(url);
        boolean channelExists = channelService.existsChannelURL(url);

        if (!correctUrl)
            mav.addObject("channelInfo", "URL was not found");
        else if (channelExists)
             mav.addObject("channelInfo", "Channel already exists");
        else
          mav.addObject("channelInfo", "Name of the channel");
          
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
    public ModelAndView saveChannel(ModelAndView mav, ChannelCommand channelCommand) {
        System.out.println("Channel = " + channelCommand.getUrl());

        mav.setViewName("redirect:/all-feeds");
        return mav;
    }
}

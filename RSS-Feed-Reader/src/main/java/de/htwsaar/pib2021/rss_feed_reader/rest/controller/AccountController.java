package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import de.htwsaar.pib2021.rss_feed_reader.config.security.SecurityUser;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.AccountService;

@Controller
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(value = { "/save-interests" }, consumes = "application/json")
    public ModelAndView saveUserInterests(
            @RequestBody List<String> interests, 
            ModelAndView mav,
            @AuthenticationPrincipal SecurityUser securityUser) {

        accountService.saveUserInterests(securityUser.getUser(), interests);
        
        mav.setViewName("redirect:/all-feeds");
        return mav;
    }

}

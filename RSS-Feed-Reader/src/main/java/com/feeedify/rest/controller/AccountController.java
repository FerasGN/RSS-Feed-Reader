package com.feeedify.rest.controller;

import static com.feeedify.constants.Endpoints.*;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.feeedify.commands.PasswordCommand;
import com.feeedify.commands.UserProfileUpdateCommand;
import com.feeedify.config.security.SecurityUser;
import com.feeedify.database.entity.User;
import com.feeedify.exceptions.UserNotFoundException;
import com.feeedify.rest.service.AccountService;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private FeedsController feedsController;

    @PostMapping(value = { SAVE_INTERESTS_URL }, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ModelAndView saveUserInterests(@RequestBody List<String> interests, ModelAndView mav,
            @AuthenticationPrincipal SecurityUser securityUser) {

        accountService.saveUserInterests(securityUser.getUser(), interests);

        mav.setViewName("redirect:/all-feeds");
        return mav;
    }

    @GetMapping(value = { ACCOUNT_URL })
    public String getAccount(Model model, @AuthenticationPrincipal SecurityUser securityUser) {

        // add categories, channels and the number of unread feeds
        model = feedsController.initSidePanelFeedsInfo(model, securityUser);
        User user = accountService.findUser(securityUser.getUsername()).get();
        UserProfileUpdateCommand userProfileUpdateCommand = accountService.convertToUserProfileUpdateCommand(user);

        model.addAttribute("userProfileUpdateCommand", userProfileUpdateCommand);
        model.addAttribute("passwordCommand", new PasswordCommand());

        return "account";
    }

    @PostMapping(value = { SAVE_PROFILE_URL }, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void saveProfile(@RequestBody UserProfileUpdateCommand userProfileUpdateCommand,
            @AuthenticationPrincipal SecurityUser securityUser) {

        userProfileUpdateCommand.setUserName(securityUser.getUsername());
        try {
            accountService.updateProfileInfo(userProfileUpdateCommand);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

    }

    @PostMapping(value = { CHANGE_PASSWORD_URL })
    public ModelAndView changePassowrd(ModelAndView mav, @Valid PasswordCommand passwordCommand,
            @AuthenticationPrincipal SecurityUser securityUser, BindingResult bindingResult) {

        // Check for any validation errors
        if (bindingResult.hasErrors()) {
            mav.addObject("org.springframework.validation.BindingResult.userCommand", bindingResult);
            mav.addObject("hasValidationErrors", true);
            mav.setViewName("redirect:/account");
            return mav;
        }
        passwordCommand.setUsername(securityUser.getUsername());
        accountService.changePassword(passwordCommand);
        mav.setViewName("redirect:/account");
        return mav;

    }

    @GetMapping("/delete/{username}")
    public String deleteUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("username") String username, Model model,  @AuthenticationPrincipal SecurityUser securityUser) {

        Optional<User> optionalUser = accountService.findUser(username);
        if(optionalUser.isPresent() && securityUser.getUsername().equals(username) ){
         accountService.delete(optionalUser.get());
         HttpSession session = request.getSession(false);
         SecurityContextHolder.clearContext();
   
         session = request.getSession(false);
         if(session != null) {
             session.invalidate();
         }
   
         for(Cookie cookie : request.getCookies()) {
             cookie.setMaxAge(0);
         }
   
         return "redirect:/login?logout";
        }

        return "redirect:/account";
    }

}

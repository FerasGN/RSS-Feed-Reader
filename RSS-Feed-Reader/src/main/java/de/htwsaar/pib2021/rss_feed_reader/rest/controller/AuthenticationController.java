package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserCommand;
import de.htwsaar.pib2021.rss_feed_reader.config.security.SecurityUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.EmailAlreadyExistException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.UsernameAlreadyExistException;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.AuthenticationService;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * @param model
     * @return String
     */
    @GetMapping(value = { "/login" })
    public String getLogIn(Model model) {
        return "authentication/login";
    }

    /**
     * @param model
     * @return String
     */
    @GetMapping(value = { "/" })
    public ModelAndView viewHome(ModelAndView mav, @AuthenticationPrincipal SecurityUser securityUser) {
        try {

            mav.setViewName("complete-profile");
            return mav;
        } catch (Exception e) {
            return mav; // Errormessage unable to login
        }
    }

    /**
     * @param model
     * @return String
     */
    @GetMapping(value = { "/signup" })
    public String getSignUp(Model model) {
        // If true, it means there was a sign up failure (Validation errors) and the
        // user was redirected to this handler again
        if (model.containsAttribute("hasValidationErrors")) {
            // the errors encountered
            BindingResult bindingResult = (BindingResult) model
                    .getAttribute("org.springframework.validation.BindingResult.userCommand");
            // the names of the fields that are in error
            List<String> errorsFields = bindingResult.getFieldErrors().stream().map(field -> field.getField())
                    .collect(Collectors.toList());
            // the user inputs
            UserCommand userCommand = (UserCommand) model.getAttribute("userCommand");
            model.addAttribute("userCommand", userCommand);
            model.addAttribute("errorsFields", errorsFields);

        } else if (model.containsAttribute("emailExist")) {

            UserCommand userCommand = (UserCommand) model.getAttribute("userCommand");
            model.addAttribute("userCommand", userCommand);
            model.addAttribute("emailExist", "true");

        } else if (model.containsAttribute("usernameExist")) {

            UserCommand userCommand = (UserCommand) model.getAttribute("userCommand");
            model.addAttribute("userCommand", userCommand);
            model.addAttribute("usernameExist", "true");

        } else {
            model.addAttribute("userCommand", new UserCommand());
        }
        return "authentication/signup";
    }

    /**
     * @param mav
     * @param userCommand
     * @param bindingResult
     * @param redAttrs
     * @return ModelAndView
     */
    @PostMapping(value = { "/signup" })
    public ModelAndView postSignUp(ModelAndView mav, @Valid UserCommand userCommand, BindingResult bindingResult,
            RedirectAttributes redAttrs) {

        // Check for any validation errors
        if (bindingResult.hasErrors()) {
            redAttrs.addFlashAttribute("org.springframework.validation.BindingResult.userCommand", bindingResult);
            redAttrs.addFlashAttribute("userCommand", userCommand);
            redAttrs.addFlashAttribute("hasValidationErrors", "true");
            mav.setViewName("redirect:/signup");
            return mav;
        }

        try {
            authenticationService.signUp(userCommand);
            mav.setViewName("redirect:/login");

        } catch (EmailAlreadyExistException ex) {

            redAttrs.addFlashAttribute("emailExist", "true");
            redAttrs.addFlashAttribute("userCommand", userCommand);
            mav.setViewName("redirect:/signup");

        } catch (UsernameAlreadyExistException ex) {

            redAttrs.addFlashAttribute("usernameExist", "true");
            redAttrs.addFlashAttribute("userCommand", userCommand);
            mav.setViewName("redirect:/signup");

        }

        return mav;
    }

    /**
     * @return String
     */
    @GetMapping(value = { "/restore-password" })
    public String restorePassword() {
        return "authentication/restore-password";
    }

    /**
     * @return String
     */
    @GetMapping(value = { "/complete-profile" })
    public String completeProfile() {
        return "complete-profile";
    }

}

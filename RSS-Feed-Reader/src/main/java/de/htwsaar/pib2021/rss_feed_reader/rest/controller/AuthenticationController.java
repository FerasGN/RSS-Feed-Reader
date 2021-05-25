package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserCommand;
import de.htwsaar.pib2021.rss_feed_reader.config.security.SecurityUser;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.EmailAlreadyExistException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.UsernameAlreadyExistException;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.AuthenticationService;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping(value = { "/signup" })
    public String getSignUp(Model model) {
        // If true, it means there was a sign up failure (Validation errors) and the
        // user was redirected to this handler again
        if (model.containsAttribute("hasValidationErrors"))
            model = showValidationErrors(model);

        else if (model.containsAttribute("emailExist") || (model.containsAttribute("usernameExist")))
            return "authentication/signup";

        else
            model.addAttribute("userCommand", new UserCommand());

        return "authentication/signup";
    }

    /**
     * @param model
     * @return Model
     */
    private Model showValidationErrors(Model model) {
        // the errors encountered
        BindingResult bindingResult = (BindingResult) model
                .getAttribute("org.springframework.validation.BindingResult.userCommand");
        // the names of the fields that are in error
        List<String> errorsFields = bindingResult.getFieldErrors().stream().map(field -> field.getField())
                .collect(Collectors.toList());
        model.addAttribute("errorsFields", errorsFields);
        return model;
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
            redAttrs.addFlashAttribute("hasValidationErrors", true);
            mav.setViewName("redirect:/signup");
            return mav;
        }

        // if there are no validation errors
        mav = trySignUp(mav, userCommand, redAttrs);

        return mav;
    }

    /**
     * @param mav
     * @param userCommand
     * @param redAttrs
     * @return ModelAndView
     */
    private ModelAndView trySignUp(ModelAndView mav, UserCommand userCommand, RedirectAttributes redAttrs) {
        try {
            authenticationService.signUp(userCommand);
            mav.setViewName("redirect:/login");

        } catch (EmailAlreadyExistException ex) {

            redAttrs.addFlashAttribute("emailExist", true);
            // redirect the same user information that contanins an error
            redAttrs.addFlashAttribute("userCommand", userCommand);
            mav.setViewName("redirect:/signup");

        } catch (UsernameAlreadyExistException ex) {

            redAttrs.addFlashAttribute("usernameExist", true);
            // redirect the same user information that contanins an error
            redAttrs.addFlashAttribute("userCommand", userCommand);
            mav.setViewName("redirect:/signup");

        }
        return mav;
    }

    /**
     * @param model
     * @return String
     */
    @GetMapping(value = { "/login" })
    public String getLogIn() {
        // if user already logged in, redirect to the home page
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "authentication/login";
    }

    // Login form with error
    @GetMapping(value = { "/login-error" })
    public ModelAndView loginError(ModelAndView mav) {
        mav.addObject("loginError", true);
        mav.setViewName("authentication/login");
        return mav;
    }

    /**
     * @param model
     * @return String
     */
    @GetMapping(value = { "/" })
    public ModelAndView viewHome(ModelAndView mav, @AuthenticationPrincipal SecurityUser securityUser) {
        // If users haven't mentioned their interests, ask them to indicate a few before
        // being directed to the homepage
        // List<String> userInterests = securityUser.getUserInterests();
        // if (userInterests.isEmpty()) {
        // mav.setViewName("complete-profile");
        // return mav;
        // }

        mav.setViewName("all-feeds");
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

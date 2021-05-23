package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.UserAlreadyExistException;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.AuthenticationService;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
    @PostMapping(value = { "/login" })
    public String postLogIn(Model model, @ModelAttribute("username") String username,
            @ModelAttribute("password") String password) {
        try {
            User user = authenticationService.login(username, password);
            // model.addAttribute("user", user)
            return "";
        } catch (Exception e) {
            return ""; // Errormessage unable to login
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
        if (model.containsAttribute("hasErrors")) {
            // the errors encountered
            BindingResult bindingResult = (BindingResult) model
                    .getAttribute("org.springframework.validation.BindingResult.userCommand");
            // the user inputs
            UserCommand userCommand = (UserCommand) model.getAttribute("userCommand");
            // the names of the fields that are in error
            List<String> errorsFields = bindingResult.getFieldErrors().stream().map(field -> field.getField())
                    .collect(Collectors.toList());

            model.addAttribute("userCommand", userCommand);
            model.addAttribute("errorsFields", errorsFields);

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
            redAttrs.addFlashAttribute("hasErrors", "true");
            mav.setViewName("redirect:/signup");
            return mav;
        }

        try {
            authenticationService.signUp(userCommand);
            mav.setViewName("redirect:/login");
        } catch (UserAlreadyExistException ex) {
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

}

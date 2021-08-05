package com.feeedify.rest.controller;

import static com.feeedify.constants.Endpoints.*;

import com.feeedify.commands.UserCommand;
import com.feeedify.config.security.SecurityUser;
import com.feeedify.database.entity.ConfirmationToken;
import com.feeedify.exceptions.EmailAlreadyExistException;
import com.feeedify.exceptions.UserCouldNotBeSavedException;
import com.feeedify.exceptions.UsernameAlreadyExistException;
import com.feeedify.rest.service.AccountService;
import com.feeedify.rest.service.AuthenticationService;
import com.feeedify.rest.service.email.ConfirmationTokenService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

	
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    /**
     * @param model
     * @return String
     */
    @GetMapping(value = { SIGN_UP_URL })
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
    @PostMapping(value = { SIGN_UP_URL })
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
            redAttrs.addFlashAttribute("signedUp", true);
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
    @GetMapping(value = { LOGIN_URL })
    public String getLogIn() {
        // if user already logged in, redirect to the home page
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "authentication/login";
    }

    // Login form with error
    @GetMapping(value = { LOGIN_ERROR_URL })
    public ModelAndView loginError(ModelAndView mav) {
        mav.addObject("loginError", true);
        mav.setViewName("authentication/login");
        return mav;
    }

    /**
     * @param model
     * @return String
     */
    @GetMapping(value = { ROOT_URL })
    public ModelAndView viewHome(ModelAndView mav, @AuthenticationPrincipal SecurityUser securityUser) {
        // If users haven't mentioned their interests, ask them to indicate a few before
        // being directed to the homepage
        List<String> userInterests = securityUser.getUserInterests();
        if (userInterests.isEmpty()) {
            List<String> interests = new ArrayList<String>(10);
            mav.addObject("interests", interests);
            mav.setViewName("complete-profile");
            return mav;
        }

        mav.setViewName("redirect:/all-feeds");
        return mav;
    }

    /**
     * @return String
     */
    @GetMapping(value = { RESOTRE_PASSWORD_URL })
    public String restorePasswordPage(Model model) {

        return "authentication/restore-password";
    }

    /**
     * @return String
     * @throws UserCouldNotBeSavedException
     */
    @PostMapping(value = { RESOTRE_PASSWORD_URL })
    public String restorePassword(@RequestParam String email) throws UserCouldNotBeSavedException {
        authenticationService.restorePassword(email);
        return "authentication/restore-password";
    }

    /**
     * @return String
     */
    @GetMapping(value = { COMPLETE_PROFILE_URL })
    public String completeProfile() {
        return "complete-profile";
    }
    
    
	@GetMapping(value = CONFIRM_ACCOUNT_URL)
	public String confirmUserAccount(ModelAndView modelAndView, @RequestParam String confirmationToken,
			final RedirectAttributes redirectAttributes) {
		ConfirmationToken token = confirmationTokenService.findByConfirmationToken(confirmationToken);
		if (token != null) {
			confirmationTokenService.updateConfirmationToken(token);
			redirectAttributes.addFlashAttribute("confirmed", "Your account has been verfied. You can now sign in.");
			return "redirect:/login";
		} else {
			modelAndView.addObject("message", "The link is invalid or broken!");
			return "error/404";
		}
	}

	
}

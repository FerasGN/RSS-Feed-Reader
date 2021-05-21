package de.htwsaar.pib2021.rss_feed_reader.rest.controller;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.rest.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping(value = {""})
    public String getLogIn(Model model){
        return "";
    }

    @PostMapping(value = {""})
    public String postLogIn(Model model, @ModelAttribute("username") String username,
                                         @ModelAttribute("password") String password){
        try {
            User user =authenticationService.login(username, password);
            //model.addAttribute("user", user)
            return "";
        }catch (Exception e){
            return""; //Errormessage unable to login
        }
    }

    @GetMapping(value = {""})
    public String getSignUp(Model model){
        User user = new User();
        //model.addAttribute("user", user)
        return "";
    }

    @PostMapping(value = {""})
    public String postSignUp(Model model, @ModelAttribute("user") User user){
        try{
            user.setEnabled(false);
            authenticationService.signUp(user);
            return""; // sign up successful
        }catch (Exception e){
            //Errormessage unable to signup
            return "";
        }
    }
}

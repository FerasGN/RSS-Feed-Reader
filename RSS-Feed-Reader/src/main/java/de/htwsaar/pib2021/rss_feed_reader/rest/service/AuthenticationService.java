package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    public User login(String username, String password){
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()){
            //user not found exception
        }
        if(!user.get().getPassword().equals(password)){
            //wrong password exception
        }
        return user.get();
    }

    public String signUp(User user){
        try {
            userRepository.save(user);
        }catch(Exception e){
            //user couldnt be saved exception
        }
        return "save successful";
    }

    public void confirmEmail(User user){
        try {
            Optional<User> user1 = userRepository.findByUsername(user.getUsername());
            user1.get().setEnabled(true);
            userRepository.save(user1.get());
        }catch (Exception e){
            //user couldnt be saved exception
        }
    }

    public void restorePassword(User user, String password){
        try {
            user.setPassword(password);
            userRepository.save(user);
        }catch (Exception e){
            //user couldnt be saved exception
        }
    }
}

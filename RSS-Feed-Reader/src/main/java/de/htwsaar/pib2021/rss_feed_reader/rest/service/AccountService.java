package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private static final String USER_NOT_FOUND = "We couldn't find any user with the username provided";

    /**
     *
     * @param username
     * @return
     * @throws UserNotFoundException
     */
    public User findUser(String username) throws UserNotFoundException {
        try {
            User user = userRepository.findByUsername(username).get();
            return user;
        } catch (NoSuchElementException e){
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    public void updateProfileInfo(User user){
        //TODO
    }

    /**
     *
     * @param user
     * @param password
     */
    public void changePassword(User user, String password){
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    /**
     *
     * @param user
     * @param interests
     */
    public void saveUserInterests(User user, List<String> interests){
        user.setUserInterests(interests);
    }

    public void changeLanguage(){
        //TODO

    }
}

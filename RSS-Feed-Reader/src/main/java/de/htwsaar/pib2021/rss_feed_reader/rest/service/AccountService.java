package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserProfileUpdateCommand;
import de.htwsaar.pib2021.rss_feed_reader.converters.UserProfileUpdateCommandToUser;
import de.htwsaar.pib2021.rss_feed_reader.converters.UserToUserProfileUpdateCommand;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private static final String USER_NOT_FOUND = "We couldn't find any user with the username provided";
    private static final String NO_USER_WITH_GIVEN_EMAIL = "No user with given email was found.";

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
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    /**
     *
     * @param userProfileUpdateCommand
     * @throws UserNotFoundException
     */
    public void updateProfileInfo(UserProfileUpdateCommand userProfileUpdateCommand) throws UserNotFoundException {

        UserProfileUpdateCommandToUser up = new UserProfileUpdateCommandToUser(passwordEncoder);
        User user = up.convert(userProfileUpdateCommand);
        Optional<User> repoUser = userRepository.findByEmail(user.getEmail());

        if (repoUser.isPresent()) {
            User repoUser_ = repoUser.get();
            repoUser_.setUsername(user.getUsername());
            repoUser_.setEmail(user.getEmail());
            repoUser_.setFirstName(user.getFirstName());
            repoUser_.setLastName(user.getLastName());
            repoUser_.setCountry(user.getCountry());
            repoUser_.setJob(user.getJob());
            repoUser_.setAge(user.getAge());
            repoUser_.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(repoUser_);
        } else {
            throw new UserNotFoundException(NO_USER_WITH_GIVEN_EMAIL);
        }

    }

    /**
     *
     * @param user
     * @param password
     */
    public void changePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    /**
     *
     * @param user
     * @param interests
     */
    public void saveUserInterests(User user, List<String> interests) {
        user.setUserInterests(interests);
        userRepository.save(user);
    }

    public UserProfileUpdateCommand convertToUserProfileUpdateCommand(User user){
        UserToUserProfileUpdateCommand  UserToUserProfileUpdateCommand = new UserToUserProfileUpdateCommand();
        return UserToUserProfileUpdateCommand.convert(user);
    }

    public void changeLanguage() {
        // TODO

    }
}

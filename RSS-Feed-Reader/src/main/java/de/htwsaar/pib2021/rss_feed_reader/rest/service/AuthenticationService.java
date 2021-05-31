package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserCommand;
import de.htwsaar.pib2021.rss_feed_reader.converters.UserCommandToUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.EmailAlreadyExistException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.UserCouldNotBeSavedException;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.UsernameAlreadyExistException;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final static String EMAIL_EXIST = "There is an account with that email address: ";
    private final static String USERNAME_EXIST = "There is an account with that username : ";
    private final static String USER_NOT_SAVED = "User could not be saved, please try again.";

    private UserRepository userRepository;
    private UserCommandToUser userCommandToUser;

    public AuthenticationService(UserRepository userRepository, UserCommandToUser userCommandToUser) {
        this.userRepository = userRepository;
        this.userCommandToUser = userCommandToUser;
    }

    /**
     * @param userCommand
     * @return User
     * @throws EmailAlreadyExistException
     * @throws UsernameAlreadyExistException
     */
    public User signUp(UserCommand userCommand) throws EmailAlreadyExistException, UsernameAlreadyExistException {
        if (emailExist(userCommand.getEmail()))
            throw new EmailAlreadyExistException(EMAIL_EXIST + userCommand.getEmail());

        if (usernameExist(userCommand.getUsername()))
            throw new UsernameAlreadyExistException(USERNAME_EXIST + userCommand.getUsername());

        User user = userCommandToUser.convert(userCommand);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        userRepository.save(user);
        
        return user;
    }

    /**
     * @param email
     * @return boolean
     */
    private boolean emailExist(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    /**
     * @param username
     * @return boolean
     */
    private boolean usernameExist(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    /**
     * @param user
     */
    public void confirmEmail(User user) throws UserCouldNotBeSavedException{
        try {
            Optional<User> user1 = userRepository.findByUsername(user.getUsername());
            user1.get().setEnabled(true);
            userRepository.save(user1.get());
        } catch (Exception e) {
            throw new UserCouldNotBeSavedException(USER_NOT_SAVED);
        }
    }

    /**
     * @param user
     * @param password
     */
    public void restorePassword(User user, String password) throws UserCouldNotBeSavedException {
        try {
            user.setPassword(password);
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserCouldNotBeSavedException(USER_NOT_SAVED);
        }
    }
}

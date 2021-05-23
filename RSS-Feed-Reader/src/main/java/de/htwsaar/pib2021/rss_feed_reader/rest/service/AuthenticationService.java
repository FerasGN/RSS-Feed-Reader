package de.htwsaar.pib2021.rss_feed_reader.rest.service;

import de.htwsaar.pib2021.rss_feed_reader.commands.UserCommand;
import de.htwsaar.pib2021.rss_feed_reader.converters.UserCommandToUser;
import de.htwsaar.pib2021.rss_feed_reader.database.entity.User;
import de.htwsaar.pib2021.rss_feed_reader.database.repository.UserRepository;
import de.htwsaar.pib2021.rss_feed_reader.exceptions.UserAlreadyExistException;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final static String EMAIL_EXIST = "There is an account with that email address: ";

    private UserRepository userRepository;
    private UserCommandToUser userCommandToUser;

    public AuthenticationService(UserRepository userRepository, UserCommandToUser userCommandToUser) {
        this.userRepository = userRepository;
        this.userCommandToUser = userCommandToUser;
    }

    
    /** 
     * @param username
     * @param password
     * @return User
     */
    public User login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            // user not found exception
        }
        if (!user.get().getPassword().equals(password)) {
            // wrong password exception
        }
        return user.get();
    }

    
    /** 
     * @param userCommand
     * @return User
     * @throws UserAlreadyExistException
     */
    public User signUp(UserCommand userCommand) throws UserAlreadyExistException {
        if (emailExist(userCommand.getEmail()))
            throw new UserAlreadyExistException(EMAIL_EXIST + userCommand.getEmail());

        User user = userCommandToUser.convert(userCommand);
        user.setEnabled(true);

        return userRepository.save(user);
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
     * @param user
     */
    public void confirmEmail(User user) {
        try {
            Optional<User> user1 = userRepository.findByUsername(user.getUsername());
            user1.get().setEnabled(true);
            userRepository.save(user1.get());
        } catch (Exception e) {
            // user couldnt be saved exception
        }
    }

    
    /** 
     * @param user
     * @param password
     */
    public void restorePassword(User user, String password) {
        try {
            user.setPassword(password);
            userRepository.save(user);
        } catch (Exception e) {
            // user couldnt be saved exception
        }
    }
}

package com.feeedify.rest.service;

import com.feeedify.commands.UserCommand;
import com.feeedify.converters.UserCommandToUser;
import com.feeedify.database.entity.User;
import com.feeedify.database.repository.UserRepository;
import com.feeedify.exceptions.EmailAlreadyExistException;
import com.feeedify.exceptions.UserCouldNotBeSavedException;
import com.feeedify.exceptions.UsernameAlreadyExistException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final static String EMAIL_EXIST = "There is an account with that email address: ";
    private final static String USERNAME_EXIST = "There is an account with that username : ";
    private final static String USER_NOT_SAVED = "User could not be saved, please try again.";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCommandToUser userCommandToUser;

    /**
     * Creates new account.
     * 
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
     * @return boolean, true wether the email exists
     */
    private boolean emailExist(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    /**
     * @param username
     * @return boolean, true wether the username exists
     */
    private boolean usernameExist(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
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

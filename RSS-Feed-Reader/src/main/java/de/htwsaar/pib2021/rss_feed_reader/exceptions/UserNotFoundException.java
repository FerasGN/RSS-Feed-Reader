package de.htwsaar.pib2021.rss_feed_reader.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String errorMessage){
        super(errorMessage);
    }

}

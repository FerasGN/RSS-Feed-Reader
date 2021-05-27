package de.htwsaar.pib2021.rss_feed_reader.exceptions;

public class UsernameAlreadyExistException extends Exception {
    public UsernameAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }

}

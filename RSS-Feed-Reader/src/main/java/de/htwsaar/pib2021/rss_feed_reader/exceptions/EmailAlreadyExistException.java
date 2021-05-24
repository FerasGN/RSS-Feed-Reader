package de.htwsaar.pib2021.rss_feed_reader.exceptions;

public class EmailAlreadyExistException extends Exception {
    public EmailAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}

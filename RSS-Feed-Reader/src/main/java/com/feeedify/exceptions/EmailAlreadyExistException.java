package com.feeedify.exceptions;

public class EmailAlreadyExistException extends Exception {
    public EmailAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}

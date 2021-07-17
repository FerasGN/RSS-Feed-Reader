package com.feeedify.exceptions;

public class UsernameAlreadyExistException extends Exception {
    public UsernameAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }

}

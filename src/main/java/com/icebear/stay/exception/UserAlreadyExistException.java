package com.icebear.stay.exception;


public class UserAlreadyExistException extends RuntimeException {
    // constructor
    public UserAlreadyExistException(String message) {
        super(message);
    }
}

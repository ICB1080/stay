package com.icebear.stay.controller;

import com.icebear.stay.exception.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


// a dedicated exception handler to handle all kinds of exceptions for every controller.
@ControllerAdvice // make Spring use CustomExceptionHandler when there’s any exceptions in the Controller code.
public class CustomExceptionHandler {
    // match each exception to the corresponding handler function.
    @ExceptionHandler(UserAlreadyExistException.class)
    public final ResponseEntity<String> handleUserAlreadyExistExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}





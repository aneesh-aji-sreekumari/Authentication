package com.example.authentication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(value = UserAlreadyExistsException.class)
    public ResponseEntity<String> userExists(){

        return new ResponseEntity<>("User already exists with same email address", HttpStatus.ALREADY_REPORTED);
    }
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<String> userDoesNotExists(){
        return new ResponseEntity<>("User with give email id does not exists", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<String> incorrectPassword(){
        return new ResponseEntity<>("Invalid Credentials", HttpStatus.NOT_ACCEPTABLE);
    }
}

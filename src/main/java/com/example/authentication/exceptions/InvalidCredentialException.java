package com.example.authentication.exceptions;

public class InvalidCredentialException extends Exception{
    public InvalidCredentialException(String msg){
        super(msg);
    }
}

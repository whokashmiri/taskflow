package com.taskflow.exception.auth;



public class UserNotAuthenticatedException extends RuntimeException{
    public UserNotAuthenticatedException(String message){
        super(message);
    }
}

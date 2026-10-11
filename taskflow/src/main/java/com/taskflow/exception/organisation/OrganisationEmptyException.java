package com.taskflow.exception.organisation;

public class OrganisationEmptyException extends RuntimeException{
    public OrganisationEmptyException(String message){
        super(message);
    }
}
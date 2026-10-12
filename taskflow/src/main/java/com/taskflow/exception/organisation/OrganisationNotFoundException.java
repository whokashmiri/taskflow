package com.taskflow.exception.organisation;

public class OrganisationNotFoundException extends RuntimeException{
    public OrganisationNotFoundException(String message){
        super(message);
    }
}

package com.taskflow.exception.organisation;

public class OrganisationAlreadyException extends RuntimeException{
    public OrganisationAlreadyException(String message){
        super(message);
    }
}

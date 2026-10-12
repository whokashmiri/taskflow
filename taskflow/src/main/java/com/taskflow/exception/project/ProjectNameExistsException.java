package com.taskflow.exception.project;

public class ProjectNameExistsException extends RuntimeException{
    public ProjectNameExistsException(String message){
        super(message);
    }
}
package com.taskflow.exception;

import com.taskflow.exception.auth.EmailAlreadyExistException;
import com.taskflow.exception.auth.InvalidCredentialsException;
import com.taskflow.exception.auth.UserNotAuthenticatedException;
import com.taskflow.exception.organisation.OrganisationAlreadyException;
import com.taskflow.exception.organisation.OrganisationEmptyException;
import com.taskflow.exception.organisation.OrganisationNotFoundException;
import com.taskflow.exception.project.ProjectNameExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<String> handleEmailAlreadyExistException(EmailAlreadyExistException emailAlreadyExistException){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(emailAlreadyExistException.getMessage());
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException invalidCredentialsException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(invalidCredentialsException.getMessage());
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<String> handleUserNotAuthenticatedException(UserNotAuthenticatedException userNotAuthenticatedException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userNotAuthenticatedException.getMessage());
    }
    @ExceptionHandler(OrganisationAlreadyException.class)
    public ResponseEntity<String> handleOrganisationAlreadyException(OrganisationAlreadyException organisationAlreadyException){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(organisationAlreadyException.getMessage());
    }

    @ExceptionHandler(OrganisationEmptyException.class)
    public ResponseEntity<String> handleOrganisationEmptyException(OrganisationEmptyException organisationEmptyException){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(organisationEmptyException.getMessage());
    }

    @ExceptionHandler(OrganisationNotFoundException.class)
    public ResponseEntity<String>handleOrganisationNotFoundException(OrganisationNotFoundException organisationNotFoundException){
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(organisationNotFoundException.getMessage());
    }

    @ExceptionHandler(ProjectNameExistsException.class)
    public ResponseEntity<String>handleProjectNameExistsException(ProjectNameExistsException projectNameExistsException){
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(projectNameExistsException.getMessage());
    }

}



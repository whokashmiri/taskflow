package com.taskflow.exception;

import com.taskflow.exception.auth.EmailAlreadyExistException;
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
}

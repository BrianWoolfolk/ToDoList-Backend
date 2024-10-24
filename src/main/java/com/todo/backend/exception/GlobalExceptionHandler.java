package com.todo.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InternalServerErrorException.class)
    public final ResponseEntity<Object> handleInternalServerErrorException(InternalServerErrorException ex,
            WebRequest request) {
        return new ResponseEntity<>("Internal Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DatabaseErrorException.class)
    public final ResponseEntity<Object> handleDatabaseErrorException(DatabaseErrorException ex, WebRequest request) {
        return new ResponseEntity<>("Database Error: " + ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ClientErrorException.class)
    public final ResponseEntity<Object> handleClientErrorException(ClientErrorException ex, WebRequest request) {
        return new ResponseEntity<>("Client Error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>("Error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

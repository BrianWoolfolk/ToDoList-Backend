package com.todo.backend.exception;

public class ValidationErrorException extends RuntimeException {
    public ValidationErrorException(String message) {
        super(message);
    }
}
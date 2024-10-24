package com.todo.backend.exception;

public class DatabaseErrorException extends RuntimeException {
    public DatabaseErrorException(String message) {
        super(message);
    }
}
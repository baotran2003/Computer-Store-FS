package com.example.ComputerStore.exception;

import org.springframework.http.HttpStatus;

public class ConflictRequestError extends ErrorResponse {
    public ConflictRequestError(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}

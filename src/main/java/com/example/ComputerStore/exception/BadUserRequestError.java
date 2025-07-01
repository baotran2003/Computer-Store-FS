package com.example.ComputerStore.exception;

import org.springframework.http.HttpStatus;

public class BadUserRequestError extends ErrorResponse {
    public BadUserRequestError(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

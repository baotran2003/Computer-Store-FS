package com.example.ComputerStore.exception;

import org.springframework.http.HttpStatus;

public class BadUser2RequestError extends ErrorResponse {
    public BadUser2RequestError(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}

package com.example.ComputerStore.exception;

import org.springframework.http.HttpStatus;

public class BadRequestError extends ErrorResponse {
    public BadRequestError(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

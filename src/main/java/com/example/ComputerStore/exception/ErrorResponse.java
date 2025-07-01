package com.example.ComputerStore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse extends RuntimeException {
    private final HttpStatus status;

    public ErrorResponse(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

package com.example.ComputerStore.exception;

import com.example.ComputerStore.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorResponse.class)
    public ResponseEntity<ApiResponse<?>> handleCustomError(ErrorResponse ex) {
        ApiResponse<?> response = ApiResponse.builder()
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericError(Exception ex) {
        ApiResponse<?> response = ApiResponse.builder()
                .error("Internal Server Error")
                .message(ex.getMessage())
                .data(null)
                .build();

        return ResponseEntity.status(500).body(response);
    }
}

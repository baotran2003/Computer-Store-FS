package com.example.ComputerStore.entity.payload;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private int status;
    private String error;
    private String message;
    private T data;
}


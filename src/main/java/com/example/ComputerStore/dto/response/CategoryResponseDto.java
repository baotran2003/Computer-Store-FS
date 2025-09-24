package com.example.ComputerStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {

    private UUID id;
    private String name;
    private String image;
    private long productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
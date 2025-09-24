package com.example.ComputerStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.example.ComputerStore.enumeric.ComponentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    
    private UUID id;
    private String name;
    private BigDecimal price;
    private String description;
    private List<String> images;
    private UUID categoryId;
    private String categoryName;
    private Integer stock;
    private ComponentType componentType;
    private Integer discount;
    private BigDecimal finalPrice; // Giá sau khi giảm giá
    
    // PC specs
    private String cpu;
    private String main;
    private String ram;
    private String storage;
    private String gpu;
    private String power;
    private String caseComputer;
    private String coolers;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Review statistics (sẽ tính sau)
    private Double averageRating;
    private Integer totalReviews;
}
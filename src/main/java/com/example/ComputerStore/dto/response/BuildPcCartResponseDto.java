package com.example.ComputerStore.dto.response;

import com.example.ComputerStore.enumeric.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * BuildPcCartResponseDto - DTO cho response build PC cart item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildPcCartResponseDto {
    
    private UUID id;
    private UUID userId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private ComponentType componentType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Product information
    private ProductInfo product;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private UUID id;
        private String name;
        private BigDecimal price;
        private List<String> images;
        private ComponentType componentType;
        private Integer stock;
        private Integer discount;
        
        // PC Components specifications  
        private String cpu;
        private String main;
        private String ram;
        private String storage;
        private String gpu;
        private String power;
        private String caseComputer;
        private String coolers;
    }
}
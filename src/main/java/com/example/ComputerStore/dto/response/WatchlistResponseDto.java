package com.example.ComputerStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.ComputerStore.enumeric.ComponentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistResponseDto {
    
    private UUID watchId;
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal finalPrice; // After discount
    private String description;
    private List<String> images;
    private Integer stock;
    private Integer discount;
    private ComponentType componentType;
    private LocalDateTime addedAt;
    
    // Category information
    private UUID categoryId;
    private String categoryName;
    
    // Additional product info
    private boolean inStock;
    private String stockStatus;
    
    // Helper nested class for product information
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private UUID id;
        private String name;
        private BigDecimal price;
        private BigDecimal finalPrice;
        private String description;
        private List<String> images;
        private Integer stock;
        private Integer discount;
        private ComponentType componentType;
        private boolean inStock;
        private String stockStatus;
        
        // Category info
        private UUID categoryId;
        private String categoryName;
    }
}
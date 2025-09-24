package com.example.ComputerStore.dto.response;

import com.example.ComputerStore.enumeric.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Trả về thông tin chi tiết cart item
public class CartResponseDto {

    private UUID cartId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String fullName;
    private String phone;
    private String address;
    private LocalDateTime addedAt;
    
    // Product information
    private ProductInCartDto product;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInCartDto {
        private UUID productId;
        private String name;
        private BigDecimal originalPrice;
        private BigDecimal finalPrice;
        private BigDecimal discount;
        private String description;
        private List<String> images;
        private Integer stock;
        private ComponentType componentType;
        private boolean inStock;
        private String stockStatus;
        
        // Category information
        private UUID categoryId;
        private String categoryName;
    }
}
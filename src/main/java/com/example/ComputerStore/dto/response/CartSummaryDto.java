package com.example.ComputerStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Tóm tắt toàn bộ giỏ hàng với thống kê
public class CartSummaryDto {

    private long totalItems;
    private int totalQuantity;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal finalTotal;
    private CartStatsDto stats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartStatsDto {
        private long pcBuildsCount;
        private long componentsCount;
        private long accessoriesCount;
        private long inStockItems;
        private long outOfStockItems;
        private long discountedItems;
        private BigDecimal averageItemPrice;
        private String mostExpensiveItem;
        private String cheapestItem;
    }
}
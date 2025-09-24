package com.example.ComputerStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistStatsDto {
    
    private long totalWatchedProducts;
    private long inStockProducts;
    private long outOfStockProducts;
    private long discountedProducts;
    
    // Category breakdown
    private WatchlistCategoryStats categoryStats;
    
    // Component type breakdown
    private WatchlistComponentStats componentStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WatchlistCategoryStats {
        private long totalCategories;
        // Add specific category counts if needed
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WatchlistComponentStats {
        private long cpuCount;
        private long vgaCount;
        private long ramCount;
        private long mainCount;
        private long storageCount;
        private long powerCount;
        private long caseCount;
        private long coolersCount;
        private long pcCount;
        private long otherCount;
    }
}
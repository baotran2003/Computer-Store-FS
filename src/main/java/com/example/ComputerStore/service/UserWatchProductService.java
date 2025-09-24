package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.request.AddToWatchlistDto;
import com.example.ComputerStore.dto.response.WatchlistResponseDto;
import com.example.ComputerStore.dto.response.WatchlistStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserWatchProductService {
    
    /**
     * Add product to user's watchlist
     */
    WatchlistResponseDto addToWatchlist(UUID userId, AddToWatchlistDto addToWatchlistDto);
    
    /**
     * Remove product from user's watchlist
     */
    void removeFromWatchlist(UUID userId, UUID productId);
    
    /**
     * Get user's watchlist with pagination
     */
    Page<WatchlistResponseDto> getUserWatchlist(UUID userId, Pageable pageable);
    
    /**
     * Get user's watchlist as simple list
     */
    List<WatchlistResponseDto> getUserWatchlistSimple(UUID userId);
    
    /**
     * Check if product is in user's watchlist
     */
    boolean isProductInWatchlist(UUID userId, UUID productId);
    
    /**
     * Get watchlist statistics for user
     */
    WatchlistStatsDto getWatchlistStats(UUID userId);
    
    /**
     * Clear all items from user's watchlist
     */
    void clearWatchlist(UUID userId);
    
    /**
     * Get count of items in user's watchlist
     */
    long getWatchlistCount(UUID userId);
    
    /**
     * Get most watched products (for admin/statistics)
     */
    List<Object[]> getMostWatchedProducts(int limit);
}
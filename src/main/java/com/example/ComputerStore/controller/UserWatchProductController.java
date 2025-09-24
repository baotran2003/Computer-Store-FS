package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.AddToWatchlistDto;
import com.example.ComputerStore.dto.response.WatchlistResponseDto;
import com.example.ComputerStore.dto.response.WatchlistStatsDto;
import com.example.ComputerStore.service.UserWatchProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserWatchProductController {

    private final UserWatchProductService userWatchProductService;

    /**
     * Add a product to user's watchlist
     * POST /api/watchlist/{userId}
     */
    @PostMapping("/{userId}")
    public ResponseEntity<WatchlistResponseDto> addToWatchlist(
            @PathVariable UUID userId,
            @Valid @RequestBody AddToWatchlistDto addToWatchlistDto) {
        
        log.info("Adding product to watchlist for user: {}", userId);
        
        try {
            WatchlistResponseDto response = userWatchProductService.addToWatchlist(userId, addToWatchlistDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Error adding product to watchlist: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove a product from user's watchlist
     * DELETE /api/watchlist/{userId}/product/{productId}
     */
    @DeleteMapping("/{userId}/product/{productId}")
    public ResponseEntity<Void> removeFromWatchlist(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        
        log.info("Removing product {} from watchlist for user: {}", productId, userId);
        
        try {
            userWatchProductService.removeFromWatchlist(userId, productId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error removing product from watchlist: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get user's watchlist with pagination
     * GET /api/watchlist/{userId}?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Page<WatchlistResponseDto>> getUserWatchlist(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting watchlist for user: {} with page: {}, size: {}", userId, page, size);
        
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<WatchlistResponseDto> watchlist = userWatchProductService.getUserWatchlist(userId, pageable);
            return ResponseEntity.ok(watchlist);
        } catch (RuntimeException e) {
            log.error("Error getting user watchlist: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get user's watchlist without pagination (simple list)
     * GET /api/watchlist/{userId}/simple
     */
    @GetMapping("/{userId}/simple")
    public ResponseEntity<List<WatchlistResponseDto>> getUserWatchlistSimple(@PathVariable UUID userId) {
        
        log.info("Getting simple watchlist for user: {}", userId);
        
        try {
            List<WatchlistResponseDto> watchlist = userWatchProductService.getUserWatchlistSimple(userId);
            return ResponseEntity.ok(watchlist);
        } catch (RuntimeException e) {
            log.error("Error getting simple watchlist: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Check if a product is in user's watchlist
     * GET /api/watchlist/{userId}/product/{productId}/exists
     */
    @GetMapping("/{userId}/product/{productId}/exists")
    public ResponseEntity<Boolean> isProductInWatchlist(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        
        log.info("Checking if product {} is in watchlist for user: {}", productId, userId);
        
        try {
            boolean exists = userWatchProductService.isProductInWatchlist(userId, productId);
            return ResponseEntity.ok(exists);
        } catch (RuntimeException e) {
            log.error("Error checking product in watchlist: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get watchlist statistics for a user
     * GET /api/watchlist/{userId}/stats
     */
    @GetMapping("/{userId}/stats")
    public ResponseEntity<WatchlistStatsDto> getWatchlistStats(@PathVariable UUID userId) {
        
        log.info("Getting watchlist statistics for user: {}", userId);
        
        try {
            WatchlistStatsDto stats = userWatchProductService.getWatchlistStats(userId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.error("Error getting watchlist stats: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Clear all items from user's watchlist
     * DELETE /api/watchlist/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearWatchlist(@PathVariable UUID userId) {
        
        log.info("Clearing watchlist for user: {}", userId);
        
        try {
            userWatchProductService.clearWatchlist(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error clearing watchlist: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get watchlist count for a user
     * GET /api/watchlist/{userId}/count
     */
    @GetMapping("/{userId}/count")
    public ResponseEntity<Long> getWatchlistCount(@PathVariable UUID userId) {
        
        log.info("Getting watchlist count for user: {}", userId);
        
        try {
            long count = userWatchProductService.getWatchlistCount(userId);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            log.error("Error getting watchlist count: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get most watched products (global statistics)
     * GET /api/watchlist/most-watched?limit=10
     */
    @GetMapping("/most-watched")
    public ResponseEntity<List<Object[]>> getMostWatchedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Getting most watched products with limit: {}", limit);
        
        try {
            List<Object[]> mostWatched = userWatchProductService.getMostWatchedProducts(limit);
            return ResponseEntity.ok(mostWatched);
        } catch (RuntimeException e) {
            log.error("Error getting most watched products: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
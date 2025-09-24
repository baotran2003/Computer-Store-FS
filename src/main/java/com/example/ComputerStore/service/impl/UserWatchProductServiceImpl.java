package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.request.AddToWatchlistDto;
import com.example.ComputerStore.dto.response.WatchlistResponseDto;
import com.example.ComputerStore.dto.response.WatchlistStatsDto;
import com.example.ComputerStore.entity.Product;
import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.entity.UserWatchProduct;
import com.example.ComputerStore.enumeric.ComponentType;
import com.example.ComputerStore.repository.ProductRepository;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.repository.UserWatchProductRepository;
import com.example.ComputerStore.service.UserWatchProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserWatchProductServiceImpl implements UserWatchProductService {

    private final UserWatchProductRepository userWatchProductRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public WatchlistResponseDto addToWatchlist(UUID userId, AddToWatchlistDto addToWatchlistDto) {
        log.info("Adding product {} to watchlist for user {}", addToWatchlistDto.getProductId(), userId);
        
        // Validate user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + userId));
        
        // Validate product exists
        Product product = productRepository.findById(addToWatchlistDto.getProductId())
            .orElseThrow(() -> new RuntimeException("Product không tồn tại với ID: " + addToWatchlistDto.getProductId()));
        
        // Check if already in watchlist
        if (userWatchProductRepository.existsByUserIdAndProductId(userId, addToWatchlistDto.getProductId())) {
            throw new RuntimeException("Sản phẩm đã có trong danh sách yêu thích");
        }
        
        // Create watchlist entry
        UserWatchProduct watchlistItem = UserWatchProduct.builder()
            .user(user)
            .product(product)
            .build();
        
        UserWatchProduct saved = userWatchProductRepository.save(watchlistItem);
        
        return convertToWatchlistResponse(saved);
    }

    @Override
    public void removeFromWatchlist(UUID userId, UUID productId) {
        log.info("Removing product {} from watchlist for user {}", productId, userId);
        
        // Find and delete watchlist item directly
        userWatchProductRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WatchlistResponseDto> getUserWatchlist(UUID userId, Pageable pageable) {
        log.info("Getting watchlist for user {} with pagination", userId);
        
        // Get all watchlist items for user
        List<UserWatchProduct> allWatchlist = userWatchProductRepository.findByUserId(userId);
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allWatchlist.size());
        List<UserWatchProduct> pageContent = allWatchlist.subList(start, end);
        
        List<WatchlistResponseDto> watchlistResponses = pageContent.stream()
            .map(this::convertToWatchlistResponse)
            .collect(Collectors.toList());
        
        return new PageImpl<>(watchlistResponses, pageable, allWatchlist.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchlistResponseDto> getUserWatchlistSimple(UUID userId) {
        log.info("Getting simple watchlist for user {}", userId);
        
        List<UserWatchProduct> watchlist = userWatchProductRepository.findByUserId(userId);
        
        return watchlist.stream()
            .map(this::convertToWatchlistResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWatchlist(UUID userId, UUID productId) {
        return userWatchProductRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public WatchlistStatsDto getWatchlistStats(UUID userId) {
        log.info("Getting watchlist statistics for user {}", userId);
        
        List<UserWatchProduct> watchlist = userWatchProductRepository.findByUserId(userId);
        
        long totalWatchedProducts = watchlist.size();
        long inStockProducts = watchlist.stream()
            .mapToLong(item -> item.getProduct().getStock() > 0 ? 1 : 0)
            .sum();
        long outOfStockProducts = totalWatchedProducts - inStockProducts;
        
        // Check for discounted products - handle BigDecimal properly
        long discountedProducts = watchlist.stream()
            .mapToLong(item -> {
                BigDecimal discount = item.getProduct().getDiscount();
                return (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) ? 1 : 0;
            })
            .sum();
        
        // Simple component stats - count by component type
        long cpuCount = watchlist.stream().mapToLong(item -> 
            ComponentType.CPU.equals(item.getProduct().getComponentType()) ? 1 : 0).sum();
        long vgaCount = watchlist.stream().mapToLong(item -> 
            ComponentType.VGA.equals(item.getProduct().getComponentType()) ? 1 : 0).sum();
        long ramCount = watchlist.stream().mapToLong(item -> 
            ComponentType.RAM.equals(item.getProduct().getComponentType()) ? 1 : 0).sum();
        long pcCount = watchlist.stream().mapToLong(item -> 
            ComponentType.PC.equals(item.getProduct().getComponentType()) ? 1 : 0).sum();
        
        WatchlistStatsDto.WatchlistComponentStats componentStats = WatchlistStatsDto.WatchlistComponentStats.builder()
            .cpuCount(cpuCount)
            .vgaCount(vgaCount)
            .ramCount(ramCount)
            .pcCount(pcCount)
            .mainCount(0L)
            .storageCount(0L)
            .powerCount(0L)
            .caseCount(0L)
            .coolersCount(0L)
            .otherCount(0L)
            .build();
        
        // Simple category stats
        long totalCategories = watchlist.stream()
            .map(item -> item.getProduct().getCategory().getId())
            .distinct()
            .count();
        
        WatchlistStatsDto.WatchlistCategoryStats categoryStats = WatchlistStatsDto.WatchlistCategoryStats.builder()
            .totalCategories(totalCategories)
            .build();
        
        return WatchlistStatsDto.builder()
            .totalWatchedProducts(totalWatchedProducts)
            .inStockProducts(inStockProducts)
            .outOfStockProducts(outOfStockProducts)
            .discountedProducts(discountedProducts)
            .componentStats(componentStats)
            .categoryStats(categoryStats)
            .build();
    }

    @Override
    public void clearWatchlist(UUID userId) {
        log.info("Clearing watchlist for user {}", userId);
        
        // Delete all watchlist items for user
        List<UserWatchProduct> userWatchlist = userWatchProductRepository.findByUserId(userId);
        userWatchProductRepository.deleteAll(userWatchlist);
    }

    @Override
    @Transactional(readOnly = true)
    public long getWatchlistCount(UUID userId) {
        return userWatchProductRepository.findByUserId(userId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMostWatchedProducts(int limit) {
        log.info("Getting most watched products with limit {}", limit);
        
        // For now return empty list - implement later when needed
        return List.of();
    }

    // Helper method to convert entity to DTO
    private WatchlistResponseDto convertToWatchlistResponse(UserWatchProduct watchlistItem) {
        Product product = watchlistItem.getProduct();
        
        // Parse images - handle null or empty images
        List<String> images = (product.getImages() != null && !product.getImages().trim().isEmpty()) 
            ? Arrays.asList(product.getImages().split(","))
            : List.of();
        
        // Calculate final price after discount
        BigDecimal finalPrice = product.getPrice();
        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = finalPrice.multiply(product.getDiscount())
                .divide(BigDecimal.valueOf(100));
            finalPrice = finalPrice.subtract(discountAmount);
        }
        
        // Determine stock status
        boolean inStock = product.getStock() > 0;
        String stockStatus = inStock ? "Còn hàng" : "Hết hàng";
        
        return WatchlistResponseDto.builder()
            .watchId(watchlistItem.getId())
            .productId(product.getId())
            .productName(product.getName())
            .price(product.getPrice())
            .finalPrice(finalPrice)
            .description(product.getDescription())
            .images(images)
            .stock(product.getStock())
            .discount(product.getDiscount() != null ? product.getDiscount().intValue() : 0)
            .componentType(product.getComponentType())
            .addedAt(watchlistItem.getCreatedAt())
            .categoryId(product.getCategory().getId())
            .categoryName(product.getCategory().getName())
            .inStock(inStock)
            .stockStatus(stockStatus)
            .build();
    }
}
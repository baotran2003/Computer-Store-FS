package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.request.AddToBuildPcCartDto;
import com.example.ComputerStore.dto.request.UpdateBuildPcCartQuantityDto;
import com.example.ComputerStore.dto.response.BuildPcCartResponseDto;
import com.example.ComputerStore.entity.BuildPcCart;
import com.example.ComputerStore.entity.Product;
import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.entity.Cart;
import com.example.ComputerStore.repository.BuildPcCartRepository;
import com.example.ComputerStore.repository.ProductRepository;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.repository.CartRepository;
import com.example.ComputerStore.service.BuildPcCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BuildPcCartServiceImpl - Implementation của BuildPcCartService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BuildPcCartServiceImpl implements BuildPcCartService {
    
    private final BuildPcCartRepository buildPcCartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    
    @Override
    @Transactional
    public BuildPcCartResponseDto addToBuildPcCart(UUID userId, AddToBuildPcCartDto addToBuildPcCartDto) {
        log.info("Adding product to build PC cart: userId={}, productId={}", userId, addToBuildPcCartDto.getProductId());
        
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        // Validate product
        Product product = productRepository.findById(addToBuildPcCartDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        
        // Check stock availability
        if (product.getStock() < addToBuildPcCartDto.getQuantity()) {
            throw new RuntimeException("Số lượng trong kho không đủ");
        }
        
        // Check if product already exists in build PC cart
        Optional<BuildPcCart> existingItem = buildPcCartRepository
                .findByUserIdAndProductId(userId, addToBuildPcCartDto.getProductId());
        
        BuildPcCart buildPcCartItem;
        
        if (existingItem.isPresent()) {
            // Update existing item
            buildPcCartItem = existingItem.get();
            int newQuantity = buildPcCartItem.getQuantity() + addToBuildPcCartDto.getQuantity();
            
            // Check stock again for new total quantity
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Số lượng trong kho không đủ");
            }
            
            buildPcCartItem.setQuantity(newQuantity);
            buildPcCartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            
        } else {
            // Create new item
            buildPcCartItem = new BuildPcCart();
            buildPcCartItem.setUser(user);
            buildPcCartItem.setProduct(product);
            buildPcCartItem.setQuantity(addToBuildPcCartDto.getQuantity());
            buildPcCartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(addToBuildPcCartDto.getQuantity())));
            buildPcCartItem.setComponentType(product.getComponentType());
        }
        
        BuildPcCart savedItem = buildPcCartRepository.save(buildPcCartItem);
        
        log.info("Product added to build PC cart successfully: buildPcCartId={}", savedItem.getId());
        return mapToResponseDto(savedItem);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BuildPcCartResponseDto> getBuildPcCart(UUID userId) {
        log.info("Getting build PC cart for user: {}", userId);
        
        List<BuildPcCart> buildPcCartItems = buildPcCartRepository.findByUserId(userId);
        
        return buildPcCartItems.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public BuildPcCartResponseDto updateQuantity(UUID userId, UpdateBuildPcCartQuantityDto updateDto) {
        log.info("Updating build PC cart quantity: userId={}, productId={}, quantity={}", 
                userId, updateDto.getProductId(), updateDto.getQuantity());
        
        BuildPcCart buildPcCartItem = buildPcCartRepository
                .findByUserIdAndProductId(userId, updateDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong build PC cart"));
        
        Product product = buildPcCartItem.getProduct();
        
        // Check stock availability
        if (product.getStock() < updateDto.getQuantity()) {
            throw new RuntimeException("Số lượng trong kho không đủ");
        }
        
        buildPcCartItem.setQuantity(updateDto.getQuantity());
        buildPcCartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(updateDto.getQuantity())));
        
        BuildPcCart savedItem = buildPcCartRepository.save(buildPcCartItem);
        
        log.info("Build PC cart quantity updated successfully");
        return mapToResponseDto(savedItem);
    }
    
    @Override
    @Transactional
    public void removeFromBuildPcCart(UUID userId, UUID productId) {
        log.info("Removing product from build PC cart: userId={}, productId={}", userId, productId);
        
        BuildPcCart buildPcCartItem = buildPcCartRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong build PC cart"));
        
        buildPcCartRepository.delete(buildPcCartItem);
        
        log.info("Product removed from build PC cart successfully");
    }
    
    @Override
    @Transactional
    public void clearBuildPcCart(UUID userId) {
        log.info("Clearing build PC cart for user: {}", userId);
        
        buildPcCartRepository.deleteByUserId(userId);
        
        log.info("Build PC cart cleared successfully");
    }
    
    @Override
    @Transactional
    public void addBuildPcCartToCart(UUID userId) {
        log.info("Moving build PC cart to regular cart for user: {}", userId);
        
        List<BuildPcCart> buildPcCartItems = buildPcCartRepository.findByUserId(userId);
        
        for (BuildPcCart buildPcCartItem : buildPcCartItems) {
            // Check if product already exists in regular cart
            Optional<Cart> existingCartItem = cartRepository
                    .findByUserIdAndProductId(userId, buildPcCartItem.getProduct().getId());
            
            if (existingCartItem.isPresent()) {
                // Update existing cart item
                Cart cartItem = existingCartItem.get();
                int newQuantity = cartItem.getQuantity() + buildPcCartItem.getQuantity();
                cartItem.setQuantity(newQuantity);
                cartItem.setTotalPrice(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(newQuantity)));
                cartRepository.save(cartItem);
                
            } else {
                // Create new cart item
                Cart cartItem = new Cart();
                cartItem.setUser(buildPcCartItem.getUser());
                cartItem.setProduct(buildPcCartItem.getProduct());
                cartItem.setQuantity(buildPcCartItem.getQuantity());
                cartItem.setTotalPrice(buildPcCartItem.getTotalPrice());
                cartRepository.save(cartItem);
            }
        }
        
        // Clear build PC cart after moving to regular cart
        buildPcCartRepository.deleteByUserId(userId);
        
        log.info("Build PC cart moved to regular cart successfully");
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPrice(UUID userId) {
        return buildPcCartRepository.getTotalPriceByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getItemCount(UUID userId) {
        return buildPcCartRepository.countByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isProductInBuildPcCart(UUID userId, UUID productId) {
        return buildPcCartRepository.existsByUserIdAndProductId(userId, productId);
    }
    
    /**
     * Map BuildPcCart entity to response DTO
     */
    private BuildPcCartResponseDto mapToResponseDto(BuildPcCart buildPcCart) {
        BuildPcCartResponseDto dto = new BuildPcCartResponseDto();
        dto.setId(buildPcCart.getId());
        dto.setUserId(buildPcCart.getUser().getId());
        dto.setProductId(buildPcCart.getProduct().getId());
        dto.setQuantity(buildPcCart.getQuantity());
        dto.setTotalPrice(buildPcCart.getTotalPrice());
        dto.setComponentType(buildPcCart.getComponentType());
        dto.setCreatedAt(buildPcCart.getCreatedAt());
        dto.setUpdatedAt(buildPcCart.getUpdatedAt());
        
        // Map product information
        Product product = buildPcCart.getProduct();
        BuildPcCartResponseDto.ProductInfo productInfo = new BuildPcCartResponseDto.ProductInfo();
        productInfo.setId(product.getId());
        productInfo.setName(product.getName());
        productInfo.setPrice(product.getPrice());
        // Parse images from JSON string to List
        if (product.getImages() != null) {
            productInfo.setImages(List.of(product.getImages().split(",")));
        }
        productInfo.setComponentType(product.getComponentType());
        productInfo.setStock(product.getStock());
        // Convert BigDecimal discount to Integer percentage
        productInfo.setDiscount(product.getDiscount() != null ? product.getDiscount().intValue() : 0);
        productInfo.setCpu(product.getCpu());
        productInfo.setMain(product.getMain());
        productInfo.setRam(product.getRam());
        productInfo.setStorage(product.getStorage());
        productInfo.setGpu(product.getGpu());
        productInfo.setPower(product.getPower());
        productInfo.setCaseComputer(product.getCaseComputer());
        productInfo.setCoolers(product.getCoolers());
        
        dto.setProduct(productInfo);
        
        return dto;
    }
}
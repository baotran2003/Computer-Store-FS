package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.request.AddToCartDto;
import com.example.ComputerStore.dto.request.UpdateCartInfoDto;
import com.example.ComputerStore.dto.request.UpdateCartQuantityDto;
import com.example.ComputerStore.dto.response.CartResponseDto;
import com.example.ComputerStore.dto.response.CartSummaryDto;
import com.example.ComputerStore.entity.BuildPcCart;
import com.example.ComputerStore.entity.Cart;
import com.example.ComputerStore.entity.Product;
import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.enumeric.ComponentType;
import com.example.ComputerStore.repository.BuildPcCartRepository;
import com.example.ComputerStore.repository.CartRepository;
import com.example.ComputerStore.repository.ProductRepository;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final BuildPcCartRepository buildPcCartRepository;

    @Override
    public CartResponseDto addToCart(UUID userId, AddToCartDto addToCartDto) {
        log.info("Adding product {} to cart for user {}", addToCartDto.getProductId(), userId);
        
        // Validate user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + userId));
        
        // Validate product exists
        Product product = productRepository.findById(addToCartDto.getProductId())
            .orElseThrow(() -> new RuntimeException("Product không tồn tại với ID: " + addToCartDto.getProductId()));
        
        // Check stock availability
        if (product.getStock() < addToCartDto.getQuantity()) {
            throw new RuntimeException("Số lượng trong kho không đủ. Còn lại: " + product.getStock());
        }
        
        // Check if product already in cart
        Optional<Cart> existingCartItem = cartRepository.findByUserIdAndProductId(userId, addToCartDto.getProductId());
        
        Cart cartItem;
        if (existingCartItem.isPresent()) {
            // Update existing cart item
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + addToCartDto.getQuantity();
            
            // Check total quantity against stock
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Tổng số lượng vượt quá tồn kho. Còn lại: " + product.getStock());
            }
            
            // Calculate new total price
            BigDecimal unitPrice = calculateUnitPrice(product);
            BigDecimal newTotalPrice = unitPrice.multiply(BigDecimal.valueOf(newQuantity));
            
            cartItem.setQuantity(newQuantity);
            cartItem.setTotalPrice(newTotalPrice);
            
        } else {
            // Create new cart item
            BigDecimal unitPrice = calculateUnitPrice(product);
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(addToCartDto.getQuantity()));
            
            cartItem = Cart.builder()
                .user(user)
                .product(product)
                .quantity(addToCartDto.getQuantity())
                .totalPrice(totalPrice)
                .build();
        }
        
        Cart savedCartItem = cartRepository.save(cartItem);
        log.info("Product added to cart successfully: {}", savedCartItem.getId());
        
        return convertToCartResponse(savedCartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponseDto> getCart(UUID userId) {
        log.info("Getting cart for user: {}", userId);
        
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        
        return cartItems.stream()
            .map(this::convertToCartResponse)
            .collect(Collectors.toList());
    }

    @Override
    public CartResponseDto updateCartQuantity(UUID userId, UpdateCartQuantityDto updateCartQuantityDto) {
        log.info("Updating cart quantity for user {} and product {}", userId, updateCartQuantityDto.getProductId());
        
        // Find cart item
        Cart cartItem = cartRepository.findByUserIdAndProductId(userId, updateCartQuantityDto.getProductId())
            .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng"));
        
        Product product = cartItem.getProduct();
        
        // Check stock availability
        if (product.getStock() < updateCartQuantityDto.getQuantity()) {
            throw new RuntimeException("Số lượng trong kho không đủ. Còn lại: " + product.getStock());
        }
        
        // Update quantity and total price
        BigDecimal unitPrice = calculateUnitPrice(product);
        BigDecimal newTotalPrice = unitPrice.multiply(BigDecimal.valueOf(updateCartQuantityDto.getQuantity()));
        
        cartItem.setQuantity(updateCartQuantityDto.getQuantity());
        cartItem.setTotalPrice(newTotalPrice);
        
        Cart updatedCartItem = cartRepository.save(cartItem);
        log.info("Cart quantity updated successfully");
        
        return convertToCartResponse(updatedCartItem);
    }

    @Override
    public void removeFromCart(UUID userId, UUID cartId) {
        log.info("Removing cart item {} for user {}", cartId, userId);
        
        Cart cartItem = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart item không tồn tại"));
        
        // Verify ownership
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền xóa cart item này");
        }
        
        cartRepository.delete(cartItem);
        log.info("Cart item removed successfully");
    }

    @Override
    public void removeProductFromCart(UUID userId, UUID productId) {
        log.info("Removing product {} from cart for user {}", productId, userId);
        
        Cart cartItem = cartRepository.findByUserIdAndProductId(userId, productId)
            .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng"));
        
        cartRepository.delete(cartItem);
        log.info("Product removed from cart successfully");
    }

    @Override
    public void clearCart(UUID userId) {
        log.info("Clearing cart for user: {}", userId);
        
        cartRepository.deleteByUserId(userId);
        log.info("Cart cleared successfully");
    }

    @Override
    public void updateCartInfo(UUID userId, UpdateCartInfoDto updateCartInfoDto) {
        log.info("Updating cart info for user: {}", userId);
        
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }
        
        // Update info for all cart items
        cartItems.forEach(cartItem -> {
            cartItem.setFullName(updateCartInfoDto.getFullName());
            cartItem.setPhone(updateCartInfoDto.getPhone());
            cartItem.setAddress(updateCartInfoDto.getAddress());
        });
        
        cartRepository.saveAll(cartItems);
        log.info("Cart info updated successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public long getCartCount(UUID userId) {
        return cartRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartTotalQuantity(UUID userId) {
        return cartRepository.getTotalQuantityByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(UUID userId) {
        return cartRepository.getTotalPriceByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryDto getCartSummary(UUID userId) {
        log.info("Getting cart summary for user: {}", userId);
        
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        
        if (cartItems.isEmpty()) {
            return CartSummaryDto.builder()
                .totalItems(0)
                .totalQuantity(0)
                .subtotal(BigDecimal.ZERO)
                .totalDiscount(BigDecimal.ZERO)
                .finalTotal(BigDecimal.ZERO)
                .stats(CartSummaryDto.CartStatsDto.builder()
                    .pcBuildsCount(0)
                    .componentsCount(0)
                    .accessoriesCount(0)
                    .inStockItems(0)
                    .outOfStockItems(0)
                    .discountedItems(0)
                    .averageItemPrice(BigDecimal.ZERO)
                    .mostExpensiveItem("N/A")
                    .cheapestItem("N/A")
                    .build())
                .build();
        }
        
        // Calculate totals
        long totalItems = cartItems.size();
        int totalQuantity = cartItems.stream().mapToInt(Cart::getQuantity).sum();
        
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            BigDecimal originalPrice = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            BigDecimal itemTotal = cartItem.getTotalPrice();
            
            subtotal = subtotal.add(originalPrice);
            totalDiscount = totalDiscount.add(originalPrice.subtract(itemTotal));
        }
        
        BigDecimal finalTotal = subtotal.subtract(totalDiscount);
        
        // Calculate stats
        CartSummaryDto.CartStatsDto stats = calculateCartStats(cartItems);
        
        return CartSummaryDto.builder()
            .totalItems(totalItems)
            .totalQuantity(totalQuantity)
            .subtotal(subtotal)
            .totalDiscount(totalDiscount)
            .finalTotal(finalTotal)
            .stats(stats)
            .build();
    }

    @Override
    public void transferFromBuildPcCart(UUID userId) {
        log.info("Transferring BuildPcCart to Cart for user: {}", userId);
        
        List<BuildPcCart> buildPcCartItems = buildPcCartRepository.findByUserId(userId);
        
        if (buildPcCartItems.isEmpty()) {
            log.info("BuildPcCart is empty for user: {}, no items to transfer", userId);
            return; 
        }
        
        // Transfer each item
        for (BuildPcCart buildPcCartItem : buildPcCartItems) {
            Optional<Cart> existingCartItem = cartRepository.findByUserIdAndProductId(
                userId, buildPcCartItem.getProduct().getId());
            
            if (existingCartItem.isPresent()) {
                // Update existing cart item
                Cart cartItem = existingCartItem.get();
                int newQuantity = cartItem.getQuantity() + buildPcCartItem.getQuantity();
                BigDecimal newTotalPrice = cartItem.getTotalPrice().add(buildPcCartItem.getTotalPrice());
                
                cartItem.setQuantity(newQuantity);
                cartItem.setTotalPrice(newTotalPrice);
                cartRepository.save(cartItem);
            } else {
                // Create new cart item
                Cart newCartItem = Cart.builder()
                    .user(buildPcCartItem.getUser())
                    .product(buildPcCartItem.getProduct())
                    .quantity(buildPcCartItem.getQuantity())
                    .totalPrice(buildPcCartItem.getTotalPrice())
                    .build();
                cartRepository.save(newCartItem);
            }
        }
        
        // Clear BuildPcCart after transfer
        buildPcCartRepository.deleteAll(buildPcCartItems);
        log.info("BuildPcCart transferred to Cart successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInCart(UUID userId, UUID productId) {
        return cartRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateCartStock(UUID userId) {
        log.info("Validating cart stock for user: {}", userId);
        
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + 
                    "' không đủ số lượng. Còn lại: " + product.getStock() + 
                    ", cần: " + cartItem.getQuantity());
            }
        }
        
        log.info("Cart stock validation passed");
    }

    // Helper methods
    private BigDecimal calculateUnitPrice(Product product) {
        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = product.getPrice().multiply(product.getDiscount())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return product.getPrice().subtract(discountAmount);
        }
        return product.getPrice();
    }

    private CartResponseDto convertToCartResponse(Cart cartItem) {
        Product product = cartItem.getProduct();
        
        // Parse images
        List<String> images = (product.getImages() != null && !product.getImages().trim().isEmpty())
            ? Arrays.asList(product.getImages().split(","))
            : List.of();
        
        // Calculate prices
        BigDecimal originalPrice = product.getPrice();
        BigDecimal finalPrice = calculateUnitPrice(product);
        
        // Stock status
        boolean inStock = product.getStock() > 0;
        String stockStatus = inStock ? "Còn hàng" : "Hết hàng";
        
        CartResponseDto.ProductInCartDto productDto = CartResponseDto.ProductInCartDto.builder()
            .productId(product.getId())
            .name(product.getName())
            .originalPrice(originalPrice)
            .finalPrice(finalPrice)
            .discount(product.getDiscount())
            .description(product.getDescription())
            .images(images)
            .stock(product.getStock())
            .componentType(product.getComponentType())
            .inStock(inStock)
            .stockStatus(stockStatus)
            .categoryId(product.getCategory().getId())
            .categoryName(product.getCategory().getName())
            .build();
        
        return CartResponseDto.builder()
            .cartId(cartItem.getId())
            .quantity(cartItem.getQuantity())
            .totalPrice(cartItem.getTotalPrice())
            .fullName(cartItem.getFullName())
            .phone(cartItem.getPhone())
            .address(cartItem.getAddress())
            .addedAt(cartItem.getCreatedAt())
            .product(productDto)
            .build();
    }
    
    private CartSummaryDto.CartStatsDto calculateCartStats(List<Cart> cartItems) {
        long pcBuildsCount = cartItems.stream()
            .mapToLong(item -> ComponentType.PC.equals(item.getProduct().getComponentType()) ? 1 : 0)
            .sum();
        
        long componentsCount = cartItems.stream()
            .mapToLong(item -> !ComponentType.PC.equals(item.getProduct().getComponentType()) &&
                              !isAccessory(item.getProduct().getComponentType()) ? 1 : 0)
            .sum();
        
        long accessoriesCount = cartItems.stream()
            .mapToLong(item -> isAccessory(item.getProduct().getComponentType()) ? 1 : 0)
            .sum();
        
        long inStockItems = cartItems.stream()
            .mapToLong(item -> item.getProduct().getStock() > 0 ? 1 : 0)
            .sum();
        
        long outOfStockItems = cartItems.size() - inStockItems;
        
        long discountedItems = cartItems.stream()
            .mapToLong(item -> {
                BigDecimal discount = item.getProduct().getDiscount();
                return (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) ? 1 : 0;
            })
            .sum();
        
        // Calculate average price
        BigDecimal totalPrice = cartItems.stream()
            .map(Cart::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageItemPrice = cartItems.isEmpty() 
            ? BigDecimal.ZERO 
            : totalPrice.divide(BigDecimal.valueOf(cartItems.size()), 2, RoundingMode.HALF_UP);
        
        // Find most/least expensive items
        String mostExpensiveItem = cartItems.stream()
            .max((a, b) -> a.getProduct().getPrice().compareTo(b.getProduct().getPrice()))
            .map(item -> item.getProduct().getName())
            .orElse("N/A");
        
        String cheapestItem = cartItems.stream()
            .min((a, b) -> a.getProduct().getPrice().compareTo(b.getProduct().getPrice()))
            .map(item -> item.getProduct().getName())
            .orElse("N/A");
        
        return CartSummaryDto.CartStatsDto.builder()
            .pcBuildsCount(pcBuildsCount)
            .componentsCount(componentsCount)
            .accessoriesCount(accessoriesCount)
            .inStockItems(inStockItems)
            .outOfStockItems(outOfStockItems)
            .discountedItems(discountedItems)
            .averageItemPrice(averageItemPrice)
            .mostExpensiveItem(mostExpensiveItem)
            .cheapestItem(cheapestItem)
            .build();
    }
    
    private boolean isAccessory(ComponentType componentType) {
        return componentType == ComponentType.MONITOR ||
               componentType == ComponentType.KEYBOARD ||
               componentType == ComponentType.MOUSE ||
               componentType == ComponentType.HEADSET;
    }
}
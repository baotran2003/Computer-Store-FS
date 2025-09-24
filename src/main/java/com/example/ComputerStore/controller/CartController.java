package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.AddToCartDto;
import com.example.ComputerStore.dto.request.UpdateCartInfoDto;
import com.example.ComputerStore.dto.request.UpdateCartQuantityDto;
import com.example.ComputerStore.dto.response.CartResponseDto;
import com.example.ComputerStore.dto.response.CartSummaryDto;
import com.example.ComputerStore.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    /**
     * Thêm sản phẩm vào giỏ hàng
     * POST /api/cart/{userId}
     */
    @PostMapping("/{userId}")
    public ResponseEntity<CartResponseDto> addToCart(
            @PathVariable UUID userId,
            @Valid @RequestBody AddToCartDto addToCartDto) {
        
        log.info("Adding product to cart for user: {}", userId);
        
        try {
            CartResponseDto response = cartService.addToCart(userId, addToCartDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Error adding product to cart: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy giỏ hàng của user
     * GET /api/cart/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartResponseDto>> getCart(@PathVariable UUID userId) {
        log.info("Getting cart for user: {}", userId);
        
        try {
            List<CartResponseDto> cartItems = cartService.getCart(userId);
            return ResponseEntity.ok(cartItems);
        } catch (RuntimeException e) {
            log.error("Error getting cart: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     * PUT /api/cart/{userId}/quantity
     */
    @PutMapping("/{userId}/quantity")
    public ResponseEntity<CartResponseDto> updateCartQuantity(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateCartQuantityDto updateCartQuantityDto) {
        
        log.info("Updating cart quantity for user: {}", userId);
        
        try {
            CartResponseDto response = cartService.updateCartQuantity(userId, updateCartQuantityDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating cart quantity: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Xóa item khỏi giỏ hàng theo cartId
     * DELETE /api/cart/{userId}/item/{cartId}
     */
    @DeleteMapping("/{userId}/item/{cartId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable UUID userId,
            @PathVariable UUID cartId) {
        
        log.info("Removing cart item {} for user: {}", cartId, userId);
        
        try {
            cartService.removeFromCart(userId, cartId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error removing from cart: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng theo productId
     * DELETE /api/cart/{userId}/product/{productId}
     */
    @DeleteMapping("/{userId}/product/{productId}")
    public ResponseEntity<Void> removeProductFromCart(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        
        log.info("Removing product {} from cart for user: {}", productId, userId);
        
        try {
            cartService.removeProductFromCart(userId, productId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error removing product from cart: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng
     * DELETE /api/cart/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {
        log.info("Clearing cart for user: {}", userId);
        
        try {
            cartService.clearCart(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error clearing cart: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cập nhật thông tin giao hàng
     * PUT /api/cart/{userId}/info
     */
    @PutMapping("/{userId}/info")
    public ResponseEntity<Void> updateCartInfo(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateCartInfoDto updateCartInfoDto) {
        
        log.info("Updating cart info for user: {}", userId);
        
        try {
            cartService.updateCartInfo(userId, updateCartInfoDto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error updating cart info: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy số lượng items trong giỏ hàng
     * GET /api/cart/{userId}/count
     */
    @GetMapping("/{userId}/count")
    public ResponseEntity<Long> getCartCount(@PathVariable UUID userId) {
        log.info("Getting cart count for user: {}", userId);
        
        try {
            long count = cartService.getCartCount(userId);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            log.error("Error getting cart count: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy tổng số lượng sản phẩm trong giỏ hàng
     * GET /api/cart/{userId}/total-quantity
     */
    @GetMapping("/{userId}/total-quantity")
    public ResponseEntity<Integer> getCartTotalQuantity(@PathVariable UUID userId) {
        log.info("Getting cart total quantity for user: {}", userId);
        
        try {
            int totalQuantity = cartService.getCartTotalQuantity(userId);
            return ResponseEntity.ok(totalQuantity);
        } catch (RuntimeException e) {
            log.error("Error getting cart total quantity: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy tổng tiền giỏ hàng
     * GET /api/cart/{userId}/total
     */
    @GetMapping("/{userId}/total")
    public ResponseEntity<BigDecimal> getCartTotal(@PathVariable UUID userId) {
        log.info("Getting cart total for user: {}", userId);
        
        try {
            BigDecimal total = cartService.getCartTotal(userId);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            log.error("Error getting cart total: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy tóm tắt giỏ hàng với thống kê
     * GET /api/cart/{userId}/summary
     */
    @GetMapping("/{userId}/summary")
    public ResponseEntity<CartSummaryDto> getCartSummary(@PathVariable UUID userId) {
        log.info("Getting cart summary for user: {}", userId);
        
        try {
            CartSummaryDto summary = cartService.getCartSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            log.error("Error getting cart summary: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Chuyển sản phẩm từ BuildPcCart sang Cart
     * POST /api/cart/{userId}/transfer-from-buildpc
     */
    @PostMapping("/{userId}/transfer-from-buildpc")
    public ResponseEntity<Void> transferFromBuildPcCart(@PathVariable UUID userId) {
        log.info("Transferring BuildPcCart to Cart for user: {}", userId);
        
        try {
            cartService.transferFromBuildPcCart(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error transferring from BuildPcCart: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Kiểm tra sản phẩm có trong giỏ hàng không
     * GET /api/cart/{userId}/product/{productId}/exists
     */
    @GetMapping("/{userId}/product/{productId}/exists")
    public ResponseEntity<Boolean> isProductInCart(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        
        log.info("Checking if product {} is in cart for user: {}", productId, userId);
        
        try {
            boolean exists = cartService.isProductInCart(userId, productId);
            return ResponseEntity.ok(exists);
        } catch (RuntimeException e) {
            log.error("Error checking product in cart: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Validate stock availability trước khi checkout
     * GET /api/cart/{userId}/validate-stock
     */
    @GetMapping("/{userId}/validate-stock")
    public ResponseEntity<Void> validateCartStock(@PathVariable UUID userId) {
        log.info("Validating cart stock for user: {}", userId);
        
        try {
            cartService.validateCartStock(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Cart stock validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
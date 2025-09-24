package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.request.AddToCartDto;
import com.example.ComputerStore.dto.request.UpdateCartInfoDto;
import com.example.ComputerStore.dto.request.UpdateCartQuantityDto;
import com.example.ComputerStore.dto.response.CartResponseDto;
import com.example.ComputerStore.dto.response.CartSummaryDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CartService {

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    CartResponseDto addToCart(UUID userId, AddToCartDto addToCartDto);

    /**
     * Lấy giỏ hàng của user
     */
    List<CartResponseDto> getCart(UUID userId);

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    CartResponseDto updateCartQuantity(UUID userId, UpdateCartQuantityDto updateCartQuantityDto);

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    void removeFromCart(UUID userId, UUID cartId);

    /**
     * Xóa sản phẩm khỏi giỏ hàng theo productId
     */
    void removeProductFromCart(UUID userId, UUID productId);

    /**
     * Xóa toàn bộ giỏ hàng
     */
    void clearCart(UUID userId);

    /**
     * Cập nhật thông tin giao hàng cho giỏ hàng
     */
    void updateCartInfo(UUID userId, UpdateCartInfoDto updateCartInfoDto);

    /**
     * Lấy tổng số items trong giỏ hàng
     */
    long getCartCount(UUID userId);

    /**
     * Lấy tổng số lượng sản phẩm trong giỏ hàng
     */
    int getCartTotalQuantity(UUID userId);

    /**
     * Lấy tổng tiền giỏ hàng
     */
    BigDecimal getCartTotal(UUID userId);

    /**
     * Lấy tóm tắt giỏ hàng với thống kê
     */
    CartSummaryDto getCartSummary(UUID userId);

    /**
     * Chuyển sản phẩm từ BuildPcCart sang Cart
     */
    void transferFromBuildPcCart(UUID userId);

    /**
     * Kiểm tra sản phẩm có trong giỏ hàng không
     */
    boolean isProductInCart(UUID userId, UUID productId);

    /**
     * Validate stock availability trước khi checkout
     */
    void validateCartStock(UUID userId);
}
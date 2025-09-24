package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.request.AddToBuildPcCartDto;
import com.example.ComputerStore.dto.request.UpdateBuildPcCartQuantityDto;
import com.example.ComputerStore.dto.response.BuildPcCartResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * BuildPcCartService - Service interface cho build PC cart functionality
 * Cho phép users build PC cart với các components khác nhau
 */
public interface BuildPcCartService {
    
    /**
     * Thêm sản phẩm vào build PC cart
     * @param userId ID của user
     * @param addToBuildPcCartDto DTO chứa thông tin sản phẩm và số lượng
     * @return BuildPcCartResponseDto
     */
    BuildPcCartResponseDto addToBuildPcCart(UUID userId, AddToBuildPcCartDto addToBuildPcCartDto);
    
    /**
     * Lấy tất cả items trong build PC cart của user
     * @param userId ID của user
     * @return List<BuildPcCartResponseDto>
     */
    List<BuildPcCartResponseDto> getBuildPcCart(UUID userId);
    
    /**
     * Cập nhật số lượng sản phẩm trong build PC cart
     * @param userId ID của user
     * @param updateDto DTO chứa productId và số lượng mới
     * @return BuildPcCartResponseDto
     */
    BuildPcCartResponseDto updateQuantity(UUID userId, UpdateBuildPcCartQuantityDto updateDto);
    
    /**
     * Xóa sản phẩm khỏi build PC cart
     * @param userId ID của user
     * @param productId ID của sản phẩm cần xóa
     */
    void removeFromBuildPcCart(UUID userId, UUID productId);
    
    /**
     * Xóa tất cả items trong build PC cart của user
     * @param userId ID của user
     */
    void clearBuildPcCart(UUID userId);
    
    /**
     * Chuyển tất cả items từ build PC cart sang cart thông thường
     * @param userId ID của user
     */
    void addBuildPcCartToCart(UUID userId);
    
    /**
     * Tính tổng giá trị build PC cart của user
     * @param userId ID của user
     * @return BigDecimal tổng giá trị
     */
    BigDecimal getTotalPrice(UUID userId);
    
    /**
     * Đếm số lượng items trong build PC cart của user
     * @param userId ID của user
     * @return long số lượng items
     */
    long getItemCount(UUID userId);
    
    /**
     * Kiểm tra xem product có trong build PC cart của user không
     * @param userId ID của user
     * @param productId ID của sản phẩm
     * @return boolean
     */
    boolean isProductInBuildPcCart(UUID userId, UUID productId);
}
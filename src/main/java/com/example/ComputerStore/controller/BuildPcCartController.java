package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.AddToBuildPcCartDto;
import com.example.ComputerStore.dto.request.UpdateBuildPcCartQuantityDto;
import com.example.ComputerStore.dto.response.ApiResponse;
import com.example.ComputerStore.dto.response.BuildPcCartResponseDto;
import com.example.ComputerStore.service.BuildPcCartService;
import com.example.ComputerStore.service.JwtService;
import com.example.ComputerStore.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * BuildPcCartController - REST API endpoints cho build PC cart functionality
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "*")
public class BuildPcCartController {
    
    private final BuildPcCartService buildPcCartService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    
    /**
     * Thêm sản phẩm vào build PC cart
     * POST /api/build-pc-cart
     */
    @PostMapping("/build-pc-cart")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BuildPcCartResponseDto>> addToBuildPcCart(
            @Valid @RequestBody AddToBuildPcCartDto addToBuildPcCartDto,
            HttpServletRequest request) {
        try {
            UUID userId = getUserIdFromRequest(request);
            
            BuildPcCartResponseDto response = buildPcCartService.addToBuildPcCart(userId, addToBuildPcCartDto);
            
            return ResponseEntity.ok(ApiResponse.success("Thêm sản phẩm vào build PC cart thành công", response));
            
        } catch (Exception e) {
            log.error("Add to build PC cart error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy tất cả items trong build PC cart của user
     * GET /api/get-cart-build-pc
     */
    @GetMapping("/get-cart-build-pc")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BuildPcCartResponseDto>>> getBuildPcCart(
            HttpServletRequest request) {
        try {
            UUID userId = getUserIdFromRequest(request);
            
            List<BuildPcCartResponseDto> buildPcCart = buildPcCartService.getBuildPcCart(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy build PC cart thành công", buildPcCart));
            
        } catch (Exception e) {
            log.error("Get build PC cart error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cập nhật số lượng sản phẩm trong build PC cart
     * POST /api/update-quantity-cart-build-pc
     */
    @PostMapping("/update-quantity-cart-build-pc")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BuildPcCartResponseDto>> updateQuantity(
            @Valid @RequestBody UpdateBuildPcCartQuantityDto updateDto,
            HttpServletRequest request) {
        try {
            UUID userId = getUserIdFromRequest(request);
            
            BuildPcCartResponseDto response = buildPcCartService.updateQuantity(userId, updateDto);
            
            return ResponseEntity.ok(ApiResponse.success("Cập nhật số lượng thành công", response));
            
        } catch (Exception e) {
            log.error("Update build PC cart quantity error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xóa sản phẩm khỏi build PC cart
     * POST /api/delete-cart-build-pc
     */
    @PostMapping("/delete-cart-build-pc")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> removeFromBuildPcCart(
            @RequestBody Map<String, UUID> request,
            HttpServletRequest httpRequest) {
        try {
            UUID userId = getUserIdFromRequest(httpRequest);
            UUID productId = request.get("productId");
            
            if (productId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Product ID không được để trống"));
            }
            
            buildPcCartService.removeFromBuildPcCart(userId, productId);
            
            return ResponseEntity.ok(ApiResponse.success("Xóa sản phẩm khỏi build PC cart thành công", "OK"));
            
        } catch (Exception e) {
            log.error("Remove from build PC cart error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xóa tất cả items trong build PC cart của user
     * POST /api/delete-all-cart-build-pc
     */
    @PostMapping("/delete-all-cart-build-pc")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> clearBuildPcCart(
            HttpServletRequest request) {
        try {
            UUID userId = getUserIdFromRequest(request);
            
            buildPcCartService.clearBuildPcCart(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Xóa tất cả build PC cart thành công", "OK"));
            
        } catch (Exception e) {
            log.error("Clear build PC cart error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Chuyển tất cả items từ build PC cart sang cart thông thường
     * POST /api/add-to-cart-build-pc
     */
    @PostMapping("/add-to-cart-build-pc")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> addBuildPcCartToCart(
            HttpServletRequest request) {
        try {
            UUID userId = getUserIdFromRequest(request);
            
            buildPcCartService.addBuildPcCartToCart(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Chuyển build PC cart sang giỏ hàng thành công", "OK"));
            
        } catch (Exception e) {
            log.error("Add build PC cart to cart error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thống kê build PC cart của user
     * GET /api/build-pc-cart/stats
     */
    @GetMapping("/build-pc-cart/stats")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBuildPcCartStats(
            HttpServletRequest request) {
        try {
            UUID userId = getUserIdFromRequest(request);
            
            BigDecimal totalPrice = buildPcCartService.getTotalPrice(userId);
            long itemCount = buildPcCartService.getItemCount(userId);
            
            Map<String, Object> stats = Map.of(
                    "totalPrice", totalPrice,
                    "itemCount", itemCount
            );
            
            return ResponseEntity.ok(ApiResponse.success("Lấy thống kê build PC cart thành công", stats));
            
        } catch (Exception e) {
            log.error("Get build PC cart stats error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Extract user ID from JWT token in request
     */
    private UUID getUserIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        String email = jwtService.extractUsername(token);
        
        // Get user by email to get UUID (since JWT stores Long userId)
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"))
                .getId();
    }
    
    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Token không hợp lệ");
    }
}
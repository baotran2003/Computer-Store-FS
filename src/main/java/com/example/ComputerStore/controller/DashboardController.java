package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.response.ApiResponse;
import com.example.ComputerStore.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    // ========== DASHBOARD & STATISTICS ENDPOINTS ==========
    
    /**
     * Get dashboard stats - GET /api/dashboard/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getDashboardStats() {
        try {
            ApiResponse<Map<String, Object>> response = dashboardService.getDashboardStats();
            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response.getData()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("Dashboard stats error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get revenue stats - GET /api/dashboard/revenue
     */
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<Object>> getRevenueStats(@RequestParam(defaultValue = "month") String period) {
        try {
            ApiResponse<Map<String, Object>> response = dashboardService.getRevenueStats(period);
            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response.getData()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("Revenue stats error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get user stats - GET /api/dashboard/users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Object>> getUserStats() {
        try {
            ApiResponse<Map<String, Object>> response = dashboardService.getUserStats();
            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response.getData()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("User stats error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get product stats - GET /api/dashboard/products
     */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Object>> getProductStats() {
        try {
            ApiResponse<Map<String, Object>> response = dashboardService.getProductStats();
            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response.getData()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("Product stats error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get order/payment stats - GET /api/dashboard/orders
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Object>> getOrderStats() {
        try {
            ApiResponse<Map<String, Object>> response = dashboardService.getOrderStats();
            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response.getData()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("Order stats error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get pie chart data - GET /api/dashboard/pie-chart
     * TODO: Implement dedicated pie chart functionality in DashboardService
     */
    @GetMapping("/pie-chart")
    public ResponseEntity<ApiResponse<Object>> getPieChartData() {
        try {
            // Temporary implementation - return basic stats
            ApiResponse<Map<String, Object>> response = dashboardService.getDashboardStats();
            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu biểu đồ tròn thành công", response.getData()));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
            }
            
        } catch (Exception e) {
            log.error("Pie chart error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

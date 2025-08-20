package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.response.ApiResponse;

import java.util.Map;

public interface DashboardService {
    
    /**
     * Get overall dashboard statistics
     */
    ApiResponse<Map<String, Object>> getDashboardStats();
    
    /**
     * Get revenue statistics by period (week, month, quarter, year)
     */
    ApiResponse<Map<String, Object>> getRevenueStats(String period);
    
    /**
     * Get user statistics
     */
    ApiResponse<Map<String, Object>> getUserStats();
    
    /**
     * Get product statistics
     */
    ApiResponse<Map<String, Object>> getProductStats();
    
    /**
     * Get payment/order statistics
     */
    ApiResponse<Map<String, Object>> getOrderStats();
}

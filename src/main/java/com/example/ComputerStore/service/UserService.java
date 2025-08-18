package com.example.ComputerStore.service;

import com.example.ComputerStore.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserService {
    
    // ========== AUTHENTICATION METHODS ==========
    
    /**
     * Đăng ký user mới
     * Match với: POST /api/register
     */
    User registerUser(String fullName, String phone, String address, String email, String password);
    
    /**
     * Đăng nhập user
     * Match với: POST /api/login
     */
    User loginUser(String email, String password);
    
    /**
     * Đăng nhập với Google
     * Match với: POST /api/login-google
     */
    User loginGoogle(String credential);
    
    /**
     * Xác thực user từ token
     * Match với: GET /api/auth
     */
    String authUser(UUID userId);
    
    /**
     * Refresh token
     * Match với: GET /api/refresh-token
     */
    String refreshToken(String refreshToken);
    
    /**
     * Logout user
     * Match với: GET /api/logout
     */
    void logout(UUID userId);
    
    // ========== USER MANAGEMENT ==========
    
    /**
     * Cập nhật thông tin user
     * Match với: POST /api/update-info-user
     */
    void updateInfoUser(UUID userId, String fullName, String address, String phone);
    
    /**
     * Cập nhật role user (admin function)
     * Match với: POST /api/update-role-user
     */
    void updateRoleUser(UUID userId, String role);
    
    /**
     * Lấy danh sách users (admin)
     * Match với: GET /api/get-users
     */
    List<User> getUsers();
    
    // ========== PASSWORD MANAGEMENT ==========
    
    /**
     * Quên mật khẩu - gửi OTP
     * Match với: POST /api/forgot-password
     */
    void forgotPassword(String email);
    
    /**
     * Reset mật khẩu với OTP
     * Match với: POST /api/reset-password
     */
    void resetPassword(String otp, String newPassword, String token);
    
    // ========== DASHBOARD & STATISTICS ==========
    
    /**
     * Lấy dashboard stats
     * Match với: GET /api/dashboard
     */
    Object getDashboardStats(LocalDate startDate, LocalDate endDate);
    
    /**
     * Lấy order statistics
     * Match với: GET /api/get-order-stats
     */
    List<Object> getOrderStats(LocalDate startDate, LocalDate endDate);
    
    /**
     * Lấy dữ liệu pie chart
     * Match với: GET /api/users/pie-chart
     */
    Object getBieuDoTron();
    
    // ========== VALIDATION METHODS ==========
    
    /**
     * Kiểm tra email đã tồn tại
     */
    boolean existsByEmail(String email);
    
    /**
     * Kiểm tra phone đã tồn tại
     */
    boolean existsByPhone(String phone);
    
    /**
     * Tìm user theo email
     */
    User getUserByEmail(String email);
    
    /**
     * Tìm user theo ID
     */
    User getUserById(UUID id);
}

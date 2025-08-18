package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.*;
import com.example.ComputerStore.dto.response.*;
import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    // ========== AUTHENTICATION ENDPOINTS ==========
    
    /**
     * Register new user - POST /api/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.registerUser(
                registrationDto.getFullName(),
                registrationDto.getPhone(),
                registrationDto.getAddress(),
                registrationDto.getEmail(),
                registrationDto.getPassword()
            );
            
            UserResponseDto userResponse = UserResponseDto.fromEntity(user);
            return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công", userResponse));
            
        } catch (Exception e) {
            log.error("Registration error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * User login - POST /api/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDto>> login(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            User user = userService.loginUser(loginDto.getEmail(), loginDto.getPassword());
            UserResponseDto userResponse = UserResponseDto.fromEntity(user);
            
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", userResponse));
            
        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Google login - POST /api/auth/google
     */
    @PostMapping("/auth/google")
    public ResponseEntity<ApiResponse<UserResponseDto>> loginGoogle(@RequestBody String credential) {
        try {
            User user = userService.loginGoogle(credential);
            UserResponseDto userResponse = UserResponseDto.fromEntity(user);
            
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập Google thành công", userResponse));
            
        } catch (Exception e) {
            log.error("Google login error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * User authentication - GET /api/auth/{userId}
     */
    @GetMapping("/auth/{userId}")
    public ResponseEntity<ApiResponse<String>> authUser(@PathVariable UUID userId) {
        try {
            String authInfo = userService.authUser(userId);
            return ResponseEntity.ok(ApiResponse.success("Xác thực thành công", authInfo));
            
        } catch (Exception e) {
            log.error("Auth error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Refresh token - POST /api/refresh-token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestParam String refreshToken) {
        try {
            String newToken = userService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success("Token làm mới thành công", newToken));
            
        } catch (Exception e) {
            log.error("Refresh token error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Logout - POST /api/logout/{userId}
     */
    @PostMapping("/logout/{userId}")
    public ResponseEntity<ApiResponse<Void>> logout(@PathVariable UUID userId) {
        try {
            userService.logout(userId);
            return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công"));
            
        } catch (Exception e) {
            log.error("Logout error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // ========== USER MANAGEMENT ENDPOINTS ==========
    
    /**
     * Update user info - PUT /api/user/update/{userId}
     */
    @PutMapping("/user/update/{userId}")
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(
            @PathVariable UUID userId,
            @RequestParam String fullName,
            @RequestParam String address,
            @RequestParam String phone) {
        try {
            userService.updateInfoUser(userId, fullName, address, phone);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công"));
            
        } catch (Exception e) {
            log.error("Update user error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update user role - PUT /api/user/role/{userId}
     */
    @PutMapping("/user/role/{userId}")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable UUID userId,
            @RequestParam String role) {
        try {
            userService.updateRoleUser(userId, role);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật quyền thành công"));
            
        } catch (Exception e) {
            log.error("Update role error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all users - GET /api/users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getUsers() {
        try {
            List<User> users = userService.getUsers();
            List<UserResponseDto> userResponses = users.stream()
                    .map(UserResponseDto::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", userResponses));
            
        } catch (Exception e) {
            log.error("Get users error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // ========== PASSWORD MANAGEMENT ENDPOINTS ==========
    
    /**
     * Forgot password - POST /api/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        try {
            userService.forgotPassword(email);
            return ResponseEntity.ok(ApiResponse.success("Mã OTP đã được gửi đến email của bạn"));
            
        } catch (Exception e) {
            log.error("Forgot password error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Reset password - POST /api/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String otp,
            @RequestParam String newPassword,
            @RequestParam String token) {
        try {
            userService.resetPassword(otp, newPassword, token);
            return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công"));
            
        } catch (Exception e) {
            log.error("Reset password error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // ========== DASHBOARD & STATISTICS ENDPOINTS ==========
    
    /**
     * Get dashboard stats - GET /api/dashboard/stats
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<Object>> getDashboardStats(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            Object stats = userService.getDashboardStats(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Lấy thống kê dashboard thành công", stats));
            
        } catch (Exception e) {
            log.error("Dashboard stats error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get order stats - GET /api/dashboard/orders
     */
    @GetMapping("/dashboard/orders")
    public ResponseEntity<ApiResponse<List<Object>>> getOrderStats(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            List<Object> stats = userService.getOrderStats(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Lấy thống kê đơn hàng thành công", stats));
            
        } catch (Exception e) {
            log.error("Order stats error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get pie chart data - GET /api/dashboard/pie-chart
     */
    @GetMapping("/dashboard/pie-chart")
    public ResponseEntity<ApiResponse<Object>> getBieuDoTron() {
        try {
            Object pieChartData = userService.getBieuDoTron();
            return ResponseEntity.ok(ApiResponse.success("Lấy dữ liệu biểu đồ tròn thành công", pieChartData));
            
        } catch (Exception e) {
            log.error("Pie chart error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // ========== VALIDATION ENDPOINTS ==========
    
    /**
     * Check email exists - GET /api/check-email/{email}
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@PathVariable String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return ResponseEntity.ok(ApiResponse.success("Kiểm tra email thành công", exists));
            
        } catch (Exception e) {
            log.error("Check email error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get user by email - GET /api/user/email/{email}
     */
    @GetMapping("/user/email/{email}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user != null) {
                UserResponseDto userResponse = UserResponseDto.fromEntity(user);
                return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", userResponse));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Không tìm thấy người dùng"));
            }
            
        } catch (Exception e) {
            log.error("Get user by email error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get user by ID - GET /api/user/{id}
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                UserResponseDto userResponse = UserResponseDto.fromEntity(user);
                return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", userResponse));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Không tìm thấy người dùng"));
            }
            
        } catch (Exception e) {
            log.error("Get user by ID error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}


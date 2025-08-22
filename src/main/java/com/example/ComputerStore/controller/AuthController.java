package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.*;
import com.example.ComputerStore.dto.response.*;
import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.service.AuthenticationService;
import com.example.ComputerStore.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    private final PasswordService passwordService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponseDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            LoginResponseDto loginResponse = authenticationService.register(
                registrationDto.getFullName(),
                registrationDto.getPhone(),
                registrationDto.getAddress(),
                registrationDto.getEmail(),
                registrationDto.getPassword()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Đăng ký thành công", loginResponse));
            
        } catch (Exception e) {
            log.error("Registration error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            LoginResponseDto loginResponse = authenticationService.login(loginDto.getEmail(), loginDto.getPassword());
            
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", loginResponse));
            
        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Google login - POST /api/auth/google
     */
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<UserResponseDto>> loginGoogle(@RequestBody String credential) {
        try {
            User user = authenticationService.loginGoogle(credential);
            UserResponseDto userResponse = UserResponseDto.fromEntity(user);
            
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập Google thành công", userResponse));
            
        } catch (Exception e) {
            log.error("Google login error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * User authentication - GET /api/auth/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<String>> authUser(@PathVariable UUID userId) {
        try {
            String authInfo = authenticationService.authUser(userId);
            return ResponseEntity.ok(ApiResponse.success("Xác thực thành công", authInfo));
            
        } catch (Exception e) {
            log.error("Auth error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(HttpServletRequest request) {
        try {
            // Extract refresh token from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Missing or invalid Authorization header. Use: Bearer <token>"));
            }
            
            String refreshToken = authHeader.substring(7); // Remove "Bearer " prefix
            String newAccessToken = authenticationService.refreshToken(refreshToken);
            
            return ResponseEntity.ok(ApiResponse.success("Token làm mới thành công", newAccessToken));
            
        } catch (Exception e) {
            log.error("Refresh token error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            // Extract token from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Missing or invalid Authorization header. Use: Bearer <token>"));
            }
            
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            authenticationService.logout(token);
            
            return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công"));
            
        } catch (Exception e) {
            log.error("Logout error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestParam String email) {
        try {
            String refreshToken = passwordService.forgotPassword(email);
            return ResponseEntity.ok(ApiResponse.<String>success("Mã OTP đã được gửi đến email của bạn", refreshToken));
            
        } catch (Exception e) {
            log.error("Forgot password error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String otp,
            @RequestParam String newPassword,
            @RequestParam String token) {
        try {
            passwordService.resetPassword(otp, newPassword, token);
            return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công"));
            
        } catch (Exception e) {
            log.error("Reset password error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

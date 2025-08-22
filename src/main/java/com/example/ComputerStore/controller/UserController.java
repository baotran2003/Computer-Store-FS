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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    // ========== USER MANAGEMENT ENDPOINTS ==========
    
    /**
     * Get all users - GET /api/users
     * Admin only endpoint
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        try {
            List<User> users = userService.getUsers();
            List<UserResponseDto> userResponses = users.stream()
                    .map(UserResponseDto::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách users thành công", userResponses));
            
        } catch (Exception e) {
            log.error("Get all users error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            UserResponseDto userResponse = UserResponseDto.fromEntity(user);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin user thành công", userResponse));
            
        } catch (Exception e) {
            log.error("Get user by ID error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            UserResponseDto userResponse = UserResponseDto.fromEntity(user);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin user thành công", userResponse));
            
        } catch (Exception e) {
            log.error("Get user by email error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update user information - PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateUserInfo(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDto updateDto) {
        try {
            userService.updateInfoUser(
                id,
                updateDto.getFullName(),
                updateDto.getAddress(),
                updateDto.getPhone()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin user thành công"));
            
        } catch (Exception e) {
            log.error("Update user info error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update user role - PUT /api/users/{id}/role
     * Admin only endpoint
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<String>> updateUserRole(
            @PathVariable UUID id,
            @RequestParam String role) {
        try {
            userService.updateRoleUser(id, role);
            
            return ResponseEntity.ok(ApiResponse.success("Cập nhật role user thành công"));
            
        } catch (Exception e) {
            log.error("Update user role error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // ========== VALIDATION ENDPOINTS ==========

    @GetMapping("/validate/email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@RequestParam String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return ResponseEntity.ok(ApiResponse.success("Kiểm tra email thành công", exists));
            
        } catch (Exception e) {
            log.error("Check email exists error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/validate/phone")
    public ResponseEntity<ApiResponse<Boolean>> checkPhoneExists(@RequestParam String phone) {
        try {
            boolean exists = userService.existsByPhone(phone);
            return ResponseEntity.ok(ApiResponse.success("Kiểm tra số điện thoại thành công", exists));
            
        } catch (Exception e) {
            log.error("Check phone exists error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get user profile with detailed information - GET /api/users/profile/{id}
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            
            // Create detailed profile response
            UserProfileDto profile = UserProfileDto.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .address(user.getAddress())
                    .isAdmin(user.getIsAdmin())
                    .typeLogin(user.getTypeLogin())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success("Lấy profile user thành công", profile));
            
        } catch (Exception e) {
            log.error("Get user profile error: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
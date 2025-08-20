package com.example.ComputerStore.service;

import com.example.ComputerStore.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    
    // ========== USER MANAGEMENT METHODS ==========
    
    /**
     * Update user information
     */
    void updateInfoUser(UUID userId, String fullName, String address, String phone);
    
    /**
     * Update user role (admin function)
     */
    void updateRoleUser(UUID userId, String role);
    
    /**
     * Get all users (admin function)
     */
    List<User> getUsers();
    
    // ========== VALIDATION METHODS ==========
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if phone exists
     */
    boolean existsByPhone(String phone);
    
    /**
     * Get user by email
     */
    User getUserByEmail(String email);
    
    /**
     * Get user by ID
     */
    User getUserById(UUID id);
}

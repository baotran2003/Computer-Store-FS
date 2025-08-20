package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.response.LoginResponseDto;
import com.example.ComputerStore.entity.User;

import java.util.UUID;

public interface AuthenticationService {
    
    /**
     * Register new user
     */
    LoginResponseDto register(String fullName, String phone, String address, String email, String password);
    
    /**
     * User login with email/password
     */
    LoginResponseDto login(String email, String password);
    
    /**
     * Google OAuth login
     */
    User loginGoogle(String credential);
    
    /**
     * Get authenticated user info (encrypted)
     */
    String authUser(UUID userId);
    
    /**
     * Refresh access token
     */
    String refreshToken(String refreshToken);
    
    /**
     * Logout user by token
     */
    void logout(String token);
}

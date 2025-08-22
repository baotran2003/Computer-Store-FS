package com.example.ComputerStore.service;

public interface PasswordService {
    
    /**
     * Send forgot password OTP to email
     */
    String forgotPassword(String email);
    
    /**
     * Reset password using OTP and token
     */
    void resetPassword(String otp, String newPassword, String token);
}

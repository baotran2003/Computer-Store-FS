package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.entity.Otp;
import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.repository.OtpRepository;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.service.JwtService;
import com.example.ComputerStore.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService {
    
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @Override
    @Transactional
    public void forgotPassword(String email) {
        log.info("Processing forgot password for email: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập email");
        }
        
        // Check if user exists
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Email không tồn tại");
        }
        
        User user = userOpt.get();
        
        // Generate OTP
        String otp = generateOTP();
        
        // Hash OTP and save
        String hashedOtp = passwordEncoder.encode(otp);
        
        Otp otpEntity = new Otp();
        otpEntity.setEmail(user.getEmail());
        otpEntity.setOtpCode(hashedOtp);
        otpEntity.setExpiresAt(java.time.LocalDateTime.now().plusMinutes(15));
        otpEntity.setIsUsed(false);
        
        otpRepository.save(otpEntity);
        
        // Send email (for now just log)
        sendForgotPasswordEmail(email, otp);
        
        log.info("Forgot password OTP sent for email: {}", email);
    }
    
    @Override
    @Transactional
    public void resetPassword(String otp, String newPassword, String token) {
        log.info("Processing password reset");
        
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng gửi yêu cầu quên mật khẩu");
        }
        
        if (otp == null || newPassword == null) {
            throw new RuntimeException("Vui lòng nhập đầy đủ thông tin");
        }
        
        try {
            // Extract email from reset token
            String email = jwtService.extractUsername(token);
            if (email == null) {
                throw new RuntimeException("Token không hợp lệ");
            }
            
            // Find latest valid OTP for this email
            Optional<Otp> otpOpt = otpRepository.findValidOtp(email, otp, java.time.LocalDateTime.now());
            if (otpOpt.isEmpty()) {
                throw new RuntimeException("Sai mã OTP hoặc đã hết hạn, vui lòng lấy OTP mới");
            }
            
            Otp otpEntity = otpOpt.get();
            
            // Verify OTP
            if (!passwordEncoder.matches(otp, otpEntity.getOtpCode())) {
                throw new RuntimeException("Sai mã OTP hoặc đã hết hạn, vui lòng lấy OTP mới");
            }
            
            // Find user
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("Người dùng không tồn tại");
            }
            
            User user = userOpt.get();
            
            // Update password
            String hashedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedPassword);
            userRepository.save(user);
            
            // Mark OTP as used
            otpEntity.setIsUsed(true);
            otpRepository.save(otpEntity);
            
            // Delete used OTP
            otpRepository.deleteByEmail(email);
            
            log.info("Password reset successful for user: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Password reset failed", e);
            throw new RuntimeException("Đặt lại mật khẩu thất bại: " + e.getMessage());
        }
    }
    
    // ========== PRIVATE HELPER METHODS ==========
    
    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
    
    private void sendForgotPasswordEmail(String email, String otp) {
        // TODO: Implement actual email sending service
        // For now, just log the OTP
        log.info("=== FORGOT PASSWORD EMAIL ===");
        log.info("To: {}", email);
        log.info("OTP Code: {}", otp);
        log.info("This OTP will expire in 15 minutes");
        log.info("=============================");
        
        // Here you would integrate with email service like:
        // - Spring Mail
        // - SendGrid
        // - AWS SES
        // emailService.sendOtpEmail(email, otp);
    }
}

package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.entity.Otp;
import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.repository.OtpRepository;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.service.JwtService;
import com.example.ComputerStore.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordServiceImpl implements PasswordService {
    
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;
    
    @Override
    @Transactional
    public String forgotPassword(String email) {
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

        // xoa tat ca otp cu truoc khi tao otp moi
        otpRepository.deleteByEmail(email);
        
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

        // Generate reset token
        String refreshToken = jwtService.generateTokenWithClaims(
                user.getEmail(),
                "password_reset",
                15 * 60 *  1000L // 15 minutes expiration
        );
        
        // Send email (for now just log)
        sendForgotPasswordEmail(email, otp);
        
        log.info("Forgot password OTP sent for email: {}", email);

        return refreshToken;
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
            
            // Find latest valid OTP for this email (không check otpCode trong query)
            List<Otp> validOtps = otpRepository.findByEmailAndIsUsedFalse(email);
            
            Otp otpEntity = null;
            boolean otpFound = false;
            
            // Tìm OTP chưa hết hạn và verify hash
            for (Otp otp_entity : validOtps) {
                if (otp_entity.getExpiresAt().isAfter(java.time.LocalDateTime.now()) && 
                    passwordEncoder.matches(otp, otp_entity.getOtpCode())) {
                    otpEntity = otp_entity;
                    otpFound = true;
                    break;
                }
            }
            
            if (!otpFound || otpEntity == null) {
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
        try {
            // Log OTP for development (console)
            log.info("===============================================");
            log.info("🔑 FORGOT PASSWORD OTP FOR DEBUGGING 🔑");
            log.info("===============================================");
            log.info("📧 Email: {}", email);
            log.info("🔢 OTP Code: {}", otp);
            log.info("⏰ Expires in: 15 minutes");
            log.info("===============================================");
            
            // Send actual email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Computer Store - Mã OTP đặt lại mật khẩu");
            message.setText(
                "Xin chào,\n\n" +
                "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Computer Store.\n\n" +
                "Mã OTP của bạn là: " + otp + "\n\n" +
                "Mã này sẽ hết hạn sau 15 phút.\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "Computer Store Team"
            );
            
            mailSender.send(message);
            log.info("✅ Email sent successfully to: {}", email);
            
        } catch (Exception e) {
            log.error("❌ Failed to send email to {}: {}", email, e.getMessage());
        }
    }
}

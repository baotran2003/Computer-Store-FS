package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.response.LoginResponseDto;
import com.example.ComputerStore.entity.ApiKey;
import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.enumeric.TypeLogin;
import com.example.ComputerStore.repository.ApiKeyRepository;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.service.AuthenticationService;
import com.example.ComputerStore.service.JwtService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${app.encryption.secret}")
    private String encryptionSecret;
    
    @Override
    @Transactional
    public LoginResponseDto register(String fullName, String phone, String address, String email, String password) {
        log.info("Registering new user with email: {}", email);
        
        // Validation
        if (fullName == null || phone == null || address == null || email == null || password == null) {
            throw new RuntimeException("Vui lòng nhập đầy đủ thông tin");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại");
        }
        
        // Hash password
        String hashedPassword = passwordEncoder.encode(password);
        
        // Create user
        User user = new User();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setAddress(address);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setTypeLogin(TypeLogin.EMAIL);
        user.setIsAdmin("0");
        
        User savedUser = userRepository.save(user);
        
        // Create API key
        createApiKeyForUser(savedUser.getId());
        
        // Generate tokens like NodeJS
        String role = "1".equals(savedUser.getIsAdmin()) ? "ADMIN" : "USER";
        String token = jwtService.generateTokenWithClaims(
            savedUser.getEmail(), 
            role, 
            Long.valueOf(savedUser.getId().toString().hashCode())
        );
        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail());
        
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return LoginResponseDto.fromUserAndTokens(savedUser, token, refreshToken);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(String email, String password) {
        log.info("Login attempt for email: {}", email);
        
        // Validation
        if (email == null || password == null) {
            throw new RuntimeException("Vui lòng nhập đầy đủ thông tin");
        }
        
        // Find user
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác");
        }
        
        User user = userOpt.get();
        
        // Check if Google login
        if (user.getTypeLogin() == TypeLogin.GOOGLE) {
            throw new RuntimeException("Tài khoản đăng nhập bằng Google");
        }
        
        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác");
        }
        
        // Create API key if not exists
        createApiKeyForUser(user.getId());
        
        // Generate tokens like NodeJS
        String role = "1".equals(user.getIsAdmin()) ? "ADMIN" : "USER";
        String token = jwtService.generateTokenWithClaims(
            user.getEmail(), 
            role, 
            Long.valueOf(user.getId().toString().hashCode())
        );
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        
        log.info("User login successful: {}", user.getId());
        return LoginResponseDto.fromUserAndTokens(user, token, refreshToken);
    }
    
    @Override
    @Transactional
    public User loginGoogle(String credential) {
        log.info("Google login attempt");
        
        // Decode Google credential (JWT token)
        Map<String, Object> googleData = decodeGoogleCredential(credential);
        String email = (String) googleData.get("email");
        String name = (String) googleData.get("name");
        
        // Check if user exists
        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;
        
        if (userOpt.isPresent()) {
            user = userOpt.get();
            log.info("Existing Google user login: {}", user.getId());
        } else {
            // Create new user
            user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setTypeLogin(TypeLogin.GOOGLE);
            user.setIsAdmin("0");
            
            user = userRepository.save(user);
            log.info("New Google user created: {}", user.getId());
        }
        
        // Create API key
        createApiKeyForUser(user.getId());
        
        return user;
    }
    
    @Override
    public String authUser(UUID userId) {
        log.info("Authenticating user: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Tài khoản không tồn tại");
        }
        
        User user = userOpt.get();
        
        // Create user info object (encrypted)
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("email", user.getEmail());
        userInfo.put("isAdmin", user.getIsAdmin());
        userInfo.put("address", user.getAddress());
        userInfo.put("phone", user.getPhone());
        
        return encryptUserInfo(userInfo);
    }
    
    @Override
    public String refreshToken(String refreshToken) {
        log.info("Refreshing token");
        
        try {
            // Validate refresh token format
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new RuntimeException("Refresh token không được để trống");
            }
            
            // Verify refresh token using JWT service
            if (!jwtService.validateToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }
            
            // Extract username from refresh token
            String username = jwtService.extractUsername(refreshToken);
            if (username == null) {
                throw new RuntimeException("Không thể extract username từ refresh token");
            }
            
            // Find user by email/username
            Optional<User> userOpt = userRepository.findByEmail(username);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User không tồn tại");
            }
            
            User user = userOpt.get();
            
            // Generate new access token
            String role = "1".equals(user.getIsAdmin()) ? "ADMIN" : "USER";
            String newAccessToken = jwtService.generateTokenWithClaims(
                user.getEmail(), 
                role, 
                Long.valueOf(user.getId().toString().hashCode())
            );
            
            log.info("Generated new access token for user: {}", user.getEmail());
            return newAccessToken;
            
        } catch (Exception e) {
            log.error("Failed to refresh token", e);
            throw new RuntimeException("Invalid refresh token");
        }
    }
    
    @Override
    @Transactional
    public void logout(String token) {
        try {
            log.info("Logging out user by token");
            
            // Validate token format
            if (token == null || token.trim().isEmpty()) {
                throw new RuntimeException("Token không được để trống");
            }
            
            // Extract username from token
            String email = jwtService.extractUsername(token);
            if (email == null) {
                throw new RuntimeException("Invalid token - không thể extract email");
            }
            
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User không tồn tại");
            }
            
            User user = userOpt.get();
            log.info("Logging out user: {} ({})", user.getId(), user.getEmail());
            
            // Delete API key to invalidate session
            apiKeyRepository.deleteByUserId(user.getId());
            
            log.info("User logged out successfully: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Logout by token failed", e);
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }
    
    // ========== PRIVATE HELPER METHODS ==========
    
    private void createApiKeyForUser(UUID userId) {
        // Check if API key already exists
        if (apiKeyRepository.existsByUserId(userId)) {
            log.info("API key already exists for user: {}", userId);
            return;
        }
        
        // Generate public and private keys (same as NodeJS)
        String apiKeyValue = generateApiKey();
        String publicKey = generateApiKey();
        String privateKey = generateApiKey();
        
        ApiKey apiKey = new ApiKey();
        apiKey.setUserId(userId);  // Direct userId like NodeJS
        apiKey.setApiKey(apiKeyValue);  // Set the api_key field
        apiKey.setPublicKey(publicKey);
        apiKey.setPrivateKey(privateKey);
        
        try {
            apiKeyRepository.save(apiKey);
            log.info("API key created successfully for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to create API key for user: {}", userId, e);
            throw new RuntimeException("Không thể tạo API key");
        }
    }
    
    private String generateApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    private String encryptUserInfo(Map<String, Object> userInfo) {
        try {
            String jsonString = objectMapper.writeValueAsString(userInfo);
            
            Key key = new SecretKeySpec(encryptionSecret.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            byte[] encryptedBytes = cipher.doFinal(jsonString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Failed to encrypt user info", e);
            return "encryption_failed";
        }
    }
    
    private Map<String, Object> decodeGoogleCredential(String credential) {
        try {
            // Decode Google JWT credential (without verification)
            String[] chunks = credential.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            return objectMapper.readValue(payload, new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            log.error("Failed to decode Google credential", e);
            // Fallback values for testing
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("email", "google.user@gmail.com");
            fallback.put("name", "Google User");
            return fallback;
        }
    }
}

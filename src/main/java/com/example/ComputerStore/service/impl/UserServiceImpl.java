package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.entity.ApiKey;
import com.example.ComputerStore.entity.Otp;
import com.example.ComputerStore.enumeric.TypeLogin;
import com.example.ComputerStore.repository.*;
import com.example.ComputerStore.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final OtpRepository otpRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final UserWatchProductRepository userWatchProductRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    // ========== AUTHENTICATION METHODS ==========
    
    @Override
    @Transactional
    public User registerUser(String fullName, String phone, String address, String email, String password) {
        log.info("Registering new user with email: {}", email);
        
        // Validation
        if (fullName == null || phone == null || address == null || email == null || password == null) {
            throw new RuntimeException("Vui lòng nhập đầy đủ thông tin");
        }
        
        // Check if email exists
        if (existsByEmail(email)) {
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
        
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }
    
    @Override
    @Transactional(readOnly = true)
    public User loginUser(String email, String password) {
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
        
        log.info("User login successful: {}", user.getId());
        return user;
    }
    
    @Override
    @Transactional
    public User loginGoogle(String credential) {
        log.info("Google login attempt");
        
        // TODO: Decode Google credential
        // Map<String, Object> googleData = decodeGoogleCredential(credential);
        // String email = (String) googleData.get("email");
        // String name = (String) googleData.get("name");
        
        // For now, mock implementation
        String email = "user@gmail.com"; // Extract from credential
        String name = "Google User"; // Extract from credential
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;
        
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            // Create new Google user
            user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setTypeLogin(TypeLogin.GOOGLE);
            user.setIsAdmin("0");
            user = userRepository.save(user);
        }
        
        // Create API key
        createApiKeyForUser(user.getId());
        
        log.info("Google login successful: {}", user.getId());
        return user;
    }
    
    @Override
    @Transactional(readOnly = true)
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
        
        // TODO: Implement AES encryption
        return encryptUserInfo(userInfo);
    }
    
    @Override
    public String refreshToken(String refreshToken) {
        log.info("Refreshing token");
        
        // TODO: Implement JWT refresh token logic
        // UUID userId = verifyRefreshToken(refreshToken);
        // return createNewToken(userId);
        
        return "new_token"; // Mock implementation
    }
    
    @Override
    @Transactional
    public void logout(UUID userId) {
        log.info("Logging out user: {}", userId);
        
        // Delete API key
        apiKeyRepository.deleteByUserId(userId);
    }
    
    // ========== USER MANAGEMENT ==========
    
    @Override
    @Transactional
    public void updateInfoUser(UUID userId, String fullName, String address, String phone) {
        log.info("Updating user info: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }
        
        User user = userOpt.get();
        user.setFullName(fullName);
        user.setAddress(address);
        user.setPhone(phone);
        userRepository.save(user);
        
        log.info("User info updated successfully: {}", userId);
    }
    
    @Override
    @Transactional
    public void updateRoleUser(UUID userId, String role) {
        log.info("Updating user role: {} to {}", userId, role);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        
        User user = userOpt.get();
        user.setIsAdmin(role);
        userRepository.save(user);
        
        log.info("User role updated successfully: {}", userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }
    
    // ========== PASSWORD MANAGEMENT ==========
    
    @Override
    @Transactional
    public void forgotPassword(String email) {
        log.info("Forgot password request for email: {}", email);
        
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Vui lòng nhập email");
        }
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Email không tồn tại");
        }
        
        // Generate OTP
        String otp = generateOTP();
        String hashedOtp = passwordEncoder.encode(otp);
        
        // Save OTP
        Otp otpEntity = new Otp();
        otpEntity.setEmail(email);
        otpEntity.setOtpCode(hashedOtp);
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        otpEntity.setIsUsed(false);
        otpRepository.save(otpEntity);
        
        // TODO: Send email with OTP
        sendForgotPasswordEmail(email, otp);
        
        log.info("OTP sent successfully to email: {}", email);
    }
    
    @Override
    @Transactional
    public void resetPassword(String otp, String newPassword, String token) {
        log.info("Password reset attempt");
        
        if (token == null) {
            throw new RuntimeException("Vui lòng gửi yêu cầu quên mật khẩu");
        }
        
        // TODO: Decode reset token to get email
        String email = extractEmailFromToken(token);
        
        // Find latest OTP
        List<Otp> otps = otpRepository.findByEmail(email);
        Optional<Otp> otpOpt = otps.stream()
                .filter(o -> !o.getIsUsed() && o.getExpiresAt().isAfter(LocalDateTime.now()))
                .max(Comparator.comparing(Otp::getCreatedAt));
        
        if (otpOpt.isEmpty()) {
            throw new RuntimeException("Sai mã OTP hoặc đã hết hạn, vui lòng lấy OTP mới");
        }
        
        Otp otpEntity = otpOpt.get();
        
        // Verify OTP
        if (!passwordEncoder.matches(otp, otpEntity.getOtpCode())) {
            throw new RuntimeException("Sai mã OTP hoặc đã hết hạn, vui lòng lấy OTP mới");
        }
        
        // Find user and update password
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        
        User user = userOpt.get();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        
        // Mark OTP as used
        otpEntity.setIsUsed(true);
        otpRepository.save(otpEntity);
        
        log.info("Password reset successfully for email: {}", email);
    }
    
    // ========== DASHBOARD & STATISTICS ==========
    
    @Override
    @Transactional(readOnly = true)
    public Object getDashboardStats(LocalDate startDate, LocalDate endDate) {
        log.info("Getting dashboard stats from {} to {}", startDate, endDate);
        
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;
        
        // Basic stats
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalWatching = userWatchProductRepository.count();
        
        // Total revenue from completed orders
        BigDecimal totalRevenue = paymentRepository.getRevenueByDateRange(start, end);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        
        // Recent orders (top 5)
        List<Object> recentOrders = new ArrayList<>(paymentRepository.findTop5ByOrderByCreatedAtDesc());
        
        // Top selling products
        List<Object[]> topProductsData = paymentRepository.getBestSellingProducts(
            org.springframework.data.domain.PageRequest.of(0, 5));
        
        Map<String, Object> dashboardStats = new HashMap<>();
        dashboardStats.put("totalUsers", totalUsers);
        dashboardStats.put("totalProducts", totalProducts);
        dashboardStats.put("totalRevenue", totalRevenue);
        dashboardStats.put("totalWatching", totalWatching);
        dashboardStats.put("recentOrders", recentOrders);
        dashboardStats.put("topProducts", topProductsData);
        
        return dashboardStats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object> getOrderStats(LocalDate startDate, LocalDate endDate) {
        log.info("Getting order stats from {} to {}", startDate, endDate);
        
        LocalDateTime start, end;
        
        if (startDate != null && endDate != null) {
            start = startDate.atStartOfDay();
            end = endDate.atTime(23, 59, 59);
        } else {
            // Default to last 7 days
            end = LocalDate.now().atTime(23, 59, 59);
            start = LocalDate.now().minusDays(6).atStartOfDay();
        }
        
        // Get orders grouped by date
        List<Object[]> orderStats = paymentRepository.getOrderStatsByDateRange(start, end);
        
        return new ArrayList<>(Arrays.asList(orderStats.toArray()));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object getBieuDoTron() {
        log.info("Getting pie chart data");
        
        // Category stats
        List<Object[]> categoryStats = productRepository.getCategoryStats();
        
        // Order status stats
        List<Object[]> orderStats = paymentRepository.getOrderStatusStats();
        
        Map<String, Object> pieChartData = new HashMap<>();
        pieChartData.put("categoryStats", categoryStats);
        pieChartData.put("orderStats", orderStats);
        
        return pieChartData;
    }
    
    // ========== VALIDATION METHODS ==========
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }
    
    // ========== PRIVATE HELPER METHODS ==========
    
    private void createApiKeyForUser(UUID userId) {
        // Check if API key already exists
        if (apiKeyRepository.existsByUserId(userId)) {
            return;
        }
        
        // Generate API keys
        String publicKey = generateApiKey();
        String privateKey = generateApiKey();
        
        ApiKey apiKey = new ApiKey();
        apiKey.setPublicKey(publicKey);
        apiKey.setPrivateKey(privateKey);
        
        // Set user relationship
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            apiKey.setUser(user);
            apiKeyRepository.save(apiKey);
        }
    }
    
    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
    
    private String generateApiKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    private String encryptUserInfo(Map<String, Object> userInfo) {
        // TODO: Implement AES encryption
        return "encrypted_user_info";
    }
    
    private String extractEmailFromToken(String token) {
        // TODO: Implement JWT token decoding
        return "user@example.com";
    }
    
    private void sendForgotPasswordEmail(String email, String otp) {
        // TODO: Implement email sending
        log.info("Sending OTP {} to email: {}", otp, email);
    }
}

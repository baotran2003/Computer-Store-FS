package com.example.ComputerStore.config;

import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.entity.Category;
import com.example.ComputerStore.enumeric.TypeLogin;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataLoader - Tạo admin user mặc định khi khởi động ứng dụng
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdminUser();
        createDefaultCategories();
    }
    
    private void createDefaultAdminUser() {
        String adminEmail = "admin@example.com";
        
        // Kiểm tra xem admin đã tồn tại chưa
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists: {}", adminEmail);
            return;
        }
        
        // Tạo admin user mới
        User adminUser = new User();
        adminUser.setFullName("Administrator");
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setPhone("0123456789");
        adminUser.setAddress("Admin Address");
        adminUser.setIsAdmin("1"); // 1 = admin, 0 = user
        adminUser.setTypeLogin(TypeLogin.EMAIL);
        
        userRepository.save(adminUser);
        log.info("✅ Default admin user created successfully!");
        log.info("📧 Email: {}", adminEmail);
        log.info("🔑 Password: admin123");
    }
    
    private void createDefaultCategories() {
        // Tạo category mặc định cho Computer Components
        if (categoryRepository.count() == 0) {
            Category computerCategory = new Category();
            computerCategory.setName("Computer Components");
            computerCategory.setImage("https://example.com/computer-components.jpg");
            
            Category saved = categoryRepository.save(computerCategory);
            log.info("✅ Default category created successfully!");
            log.info("📁 Category Name: {}", saved.getName());
            log.info("🆔 Category UUID: {}", saved.getId());
            log.info("💡 Use this UUID in Postman: {}", saved.getId());
        } else {
            // Lấy category đầu tiên để hiển thị UUID
            Category firstCategory = categoryRepository.findAll().get(0);
            log.info("📁 Existing category found: {}", firstCategory.getName());
            log.info("🆔 Category UUID: {}", firstCategory.getId());
            log.info("💡 Use this UUID in Postman: {}", firstCategory.getId());
        }
    }
}
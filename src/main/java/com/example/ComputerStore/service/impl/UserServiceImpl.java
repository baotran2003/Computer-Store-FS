package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void updateInfoUser(UUID userId, String fullName, String address, String phone) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
            
            User user = userOpt.get();
            
            // Log current user info for debugging
            log.info("Updating user: ID={}, current phone={}, new phone={}", userId, user.getPhone(), phone);
            
            // Check if phone number is already used by another user
            if (phone != null && !phone.trim().isEmpty() && !phone.equals(user.getPhone())) {
                Optional<User> existingUserWithPhone = userRepository.findFirstByPhone(phone.trim());
                if (existingUserWithPhone.isPresent() && !existingUserWithPhone.get().getId().equals(userId)) {
                    log.error("Phone number {} already exists for user ID: {}", phone, existingUserWithPhone.get().getId());
                    throw new RuntimeException("Số điện thoại đã được sử dụng bởi tài khoản khác");
                }
            }
            
            // Update user information
            if (fullName != null && !fullName.trim().isEmpty()) {
                user.setFullName(fullName.trim());
            }
            if (address != null && !address.trim().isEmpty()) {
                user.setAddress(address.trim());
            }
            if (phone != null && !phone.trim().isEmpty()) {
                user.setPhone(phone.trim());
            }
            
            userRepository.save(user);
            
        } catch (Exception e) {
            throw new RuntimeException("Error updating user info: " + e.getMessage());
        }
    }

    @Override
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }
            
            User user = userOpt.get();
            
            // Check if user is Google login (no password)
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new RuntimeException("Tài khoản Google không thể thay đổi mật khẩu");
            }
            
            // Check if current password is correct using passwordEncoder
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new RuntimeException("Mật khẩu hiện tại không đúng");
            }
            
            // Encode and update new password
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedNewPassword);
            userRepository.save(user);
            
        } catch (Exception e) {
            throw new RuntimeException("Error changing password: " + e.getMessage());
        }
    }

    @Override
    public void updateRoleUser(UUID userId, String role) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setIsAdmin(role);
                userRepository.save(user);
            } else {
                throw new RuntimeException("User not found with ID: " + userId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating user role: " + e.getMessage());
        }
    }

    @Override
    public List<User> getUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all users: " + e.getMessage());
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            return userRepository.existsByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Error checking email existence: " + e.getMessage());
        }
    }

    @Override
    public boolean existsByPhone(String phone) {
        try {
            return userRepository.existsByPhone(phone);
        } catch (Exception e) {
            throw new RuntimeException("Error checking phone existence: " + e.getMessage());
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return userOpt.get();
            } else {
                throw new RuntimeException("User not found with email: " + email);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting user by email: " + e.getMessage());
        }
    }

    @Override
    public User getUserById(UUID id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                return userOpt.get();
            } else {
                throw new RuntimeException("User not found with ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting user by ID: " + e.getMessage());
        }
    }
}
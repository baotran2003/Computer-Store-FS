package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void updateInfoUser(UUID userId, String fullName, String address, String phone) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setFullName(fullName);
                user.setAddress(address);
                user.setPhone(phone);
                userRepository.save(user);
            } else {
                throw new RuntimeException("User not found with ID: " + userId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating user info: " + e.getMessage());
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
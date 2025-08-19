package com.example.ComputerStore.dto.response;

import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.enumeric.TypeLogin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private TypeLogin typeLogin;
    private String isAdmin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Convert from Entity to DTO
    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .email(user.getEmail())
                .typeLogin(user.getTypeLogin())
                .isAdmin(user.getIsAdmin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

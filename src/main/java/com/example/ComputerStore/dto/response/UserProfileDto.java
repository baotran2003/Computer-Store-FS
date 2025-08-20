package com.example.ComputerStore.dto.response;

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
public class UserProfileDto {
    
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String isAdmin;
    private TypeLogin typeLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

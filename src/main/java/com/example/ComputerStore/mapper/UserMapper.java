package com.example.ComputerStore.mapper;

import com.example.ComputerStore.dto.response.UserResponseDto;
import com.example.ComputerStore.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for User entity and UserResponseDto
 */
@Component
public class UserMapper implements BaseMapper<User, UserResponseDto> {

    @Override
    public UserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
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

    @Override
    public User toEntity(UserResponseDto dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setId(dto.getId());
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setEmail(dto.getEmail());
        user.setTypeLogin(dto.getTypeLogin());
        user.setIsAdmin(dto.getIsAdmin());
        // Note: createdAt and updatedAt are auto-managed by JPA
        
        return user;
    }

    @Override
    public List<UserResponseDto> toDtoList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> toEntityList(List<UserResponseDto> dtos) {
        if (dtos == null) {
            return null;
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Convert User to DTO with tokens for login response
     * @param user the user entity
     * @param accessToken the access token
     * @param refreshToken the refresh token
     * @return LoginResponseDto
     */
    public com.example.ComputerStore.dto.response.LoginResponseDto toLoginResponseDto(
            User user, String accessToken, String refreshToken) {
        
        return com.example.ComputerStore.dto.response.LoginResponseDto.builder()
                .user(this.toDto(user))
                .token(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

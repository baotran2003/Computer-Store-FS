package com.example.ComputerStore.dto.response;

import com.example.ComputerStore.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private UserResponseDto user;
    private String token;
    private String refreshToken;
    
    public static LoginResponseDto fromUserAndTokens(User user, String token, String refreshToken) {
        return LoginResponseDto.builder()
                .user(UserResponseDto.fromEntity(user))
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }
}

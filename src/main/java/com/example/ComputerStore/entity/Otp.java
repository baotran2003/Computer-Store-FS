package com.example.ComputerStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Otp extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "otp_code", nullable = false)
    private String otpCode;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_used", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUsed = false;
}

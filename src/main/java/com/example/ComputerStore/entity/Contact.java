package com.example.ComputerStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "is_read", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead = false;
}

package com.example.ComputerStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "contact")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String option1;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String option2;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String option3;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String option4;
}

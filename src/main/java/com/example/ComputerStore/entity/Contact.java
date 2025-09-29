package com.example.ComputerStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Contact Entity for PC Build Consultation Requests
 * Stores customer consultation requests with their requirements and preferences
 */
@Entity
@Table(name = "contacts")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    /**
     * Customer's phone number for consultation callback
     */
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    /**
     * Customer's email (optional)
     */
    @Column(name = "email", nullable = true, length = 255)
    private String email;

    /**
     * Additional message/notes (optional)
     */
    @Column(name = "message", nullable = true, columnDefinition = "TEXT")
    private String message;

    /**
     * Option 1: Purchase intention
     * Examples: "Có", "Không", "Tham khảo (mua sau)", "Muốn được tư vấn cấu hình rồi mua"
     */
    @Column(name = "option1", nullable = false, columnDefinition = "TEXT")
    private String option1;

    /**
     * Option 2: Purpose/Usage type
     * Examples: "Chơi Game nặng + Livestream", "Làm việc đồ họa 3D", "Dựng phim-Render Video"
     */
    @Column(name = "option2", nullable = false, columnDefinition = "TEXT")
    private String option2;

    /**
     * Option 3: Budget range
     * Examples: "PC 7-8 Triệu", "PC 25 triệu", "PC 150-200 Triệu Super MAX Luxury"
     */
    @Column(name = "option3", nullable = false, columnDefinition = "TEXT")
    private String option3;

    /**
     * Option 4: Delivery/Payment method
     * Examples: "Cọc 10% giá trị đơn hàng", "Thanh toán toàn bộ nhận hàng nhanh nhất"
     */
    @Column(name = "option4", nullable = false, columnDefinition = "TEXT")
    private String option4;
}

package com.example.ComputerStore.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Contact response data")
public class ContactResponseDto {

    @Schema(description = "Contact unique identifier")
    private UUID id;

    @Schema(description = "Customer's full name", example = "Nguyễn Văn A")
    private String fullName;

    @Schema(description = "Customer's phone number", example = "0901234567")
    private String phone;

    @Schema(description = "Customer's email", example = "customer@email.com")
    private String email;

    @Schema(description = "Additional message or notes", example = "Customer notes")
    private String message;

    @Schema(description = "Purchase intention", example = "Có")
    private String option1;

    @Schema(description = "Usage purpose", example = "Chơi Game nặng + Livestream")
    private String option2;

    @Schema(description = "Budget range", example = "PC 25 triệu")
    private String option3;

    @Schema(description = "Delivery/Payment method", example = "Cọc 10%")
    private String option4;

    @Schema(description = "Contact creation time", example = "2025-09-29 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createdAt;

    @Schema(description = "Contact last update time", example = "2025-09-29 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime updatedAt;
}
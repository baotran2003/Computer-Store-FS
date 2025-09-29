package com.example.ComputerStore.dto.response;

import com.example.ComputerStore.enumeric.PaymentStatus;
import com.example.ComputerStore.enumeric.PaymentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO trả về thông tin chi tiết một payment record
 * Sử dụng cho admin management và user order history
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

    private UUID id;
    private String idPayment;
    private PaymentStatus status;
    private PaymentType typePayment;
    private BigDecimal totalPrice;
    private String fullName;
    private String phone;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private LocalDateTime updatedAt;
    
    // User information
    private UserInPaymentDto user;
    
    // Products in this payment
    private List<ProductInPaymentDto> products;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInPaymentDto {
        private UUID userId;
        private String email;
        private String fullName;
        private String phone;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInPaymentDto {
        private UUID productId;
        private String name;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalPrice;
        private String images;
        private String categoryName;
    }
}
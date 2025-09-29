package com.example.ComputerStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về URL thanh toán cho payment gateway (MOMO/VNPAY)
 * Hoặc thông báo thành công cho COD
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentUrlResponseDto {

    private String paymentId;
    private String paymentUrl; // URL redirect cho MOMO/VNPAY
    private String paymentType;
    private String message;
    private java.math.BigDecimal totalAmount;
    
    // For COD payments
    private boolean isDirectPayment; // true for COD, false for gateway
}
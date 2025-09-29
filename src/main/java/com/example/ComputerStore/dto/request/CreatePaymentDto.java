package com.example.ComputerStore.dto.request;

import com.example.ComputerStore.enumeric.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO nhận request tạo thanh toán mới từ cart
 * Hỗ trợ COD, MOMO, VNPAY payment methods
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentDto {

    @NotNull(message = "User ID không được để trống")
    private UUID userId;

    @NotNull(message = "Loại thanh toán không được để trống")
    private PaymentType typePayment;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 10, max = 255, message = "Địa chỉ phải từ 10-255 ký tự")
    private String address;

    // Optional: Redirect URLs cho payment gateway
    private String returnUrl;
    private String cancelUrl;
}
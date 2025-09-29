package com.example.ComputerStore.dto.request;

import com.example.ComputerStore.enumeric.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cập nhật trạng thái đơn hàng (Admin only)
 * pending -> completed -> delivered hoặc cancelled
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePaymentStatusDto {

    @NotBlank(message = "Payment ID không được để trống")
    private String idPayment;

    @NotNull(message = "Trạng thái không được để trống")
    private PaymentStatus status;

    // Optional: Admin note
    private String adminNote;
}
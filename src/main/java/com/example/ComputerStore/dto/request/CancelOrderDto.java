package com.example.ComputerStore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO hủy đơn hàng (User có thể hủy đơn pending)
 * Chỉ cho phép hủy đơn có status = PENDING
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelOrderDto {

    @NotBlank(message = "Order ID không được để trống")
    private String orderId;

    // Optional: ly do huy
    private String reason;
}
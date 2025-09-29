package com.example.ComputerStore.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO trả về thông tin chi tiết một đơn hàng cụ thể
 * Bao gồm thông tin giao hàng và danh sách sản phẩm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponseDto {

    private String fullName;
    private String phone; 
    private String address;
    private String typePayment;
    private java.math.BigDecimal totalPrice;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private java.time.LocalDateTime createdAt;
    private List<ProductInOrderDetailDto> products;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInOrderDetailDto {
        private java.util.UUID productId;
        private String name;
        private java.math.BigDecimal price;
        private Integer quantity;
        private String images;
    }
}
package com.example.ComputerStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO trả về danh sách đơn hàng được group theo idPayment
 * Mỗi order có thể chứa nhiều products khác nhau
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderListResponseDto {

    private String orderId;
    private String orderDate;
    private java.math.BigDecimal totalAmount;
    private String status;
    private String typePayment;
    private List<ProductInOrderDto> products;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInOrderDto {
        private java.util.UUID id;
        private Integer quantity;
        private ProductDetailDto product;
        private String images;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDetailDto {
        private java.util.UUID id;
        private String name;
        private java.math.BigDecimal price;
        private String description;
        private String categoryName;
    }
}
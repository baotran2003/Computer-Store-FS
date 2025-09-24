package com.example.ComputerStore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * UpdateBuildPcCartQuantityDto - DTO cho request cập nhật số lượng trong build PC cart
 */
@Data
public class UpdateBuildPcCartQuantityDto {
    
    @NotNull(message = "Product ID không được để trống")
    private UUID productId;
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
}
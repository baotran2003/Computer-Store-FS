package com.example.ComputerStore.dto.request;

import com.example.ComputerStore.enumeric.ComponentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * AddToBuildPcCartDto - DTO cho request thêm sản phẩm vào build PC cart
 */
@Data
public class AddToBuildPcCartDto {
    
    @NotNull(message = "Product ID không được để trống")
    private UUID productId;
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
    
    // Optional: Component type cho validation
    private ComponentType componentType;
}
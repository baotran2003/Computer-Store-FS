package com.example.ComputerStore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.example.ComputerStore.enumeric.ComponentType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDto {
    
    private String name;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0")
    private BigDecimal price;
    
    private String description;
    private List<String> images;
    private UUID categoryId;
    
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer stock;
    
    private ComponentType componentType;
    private Integer discount;
    
    // PC specs (optional)
    private String cpu;
    private String main;
    private String ram;
    private String storage;
    private String gpu;
    private String power;
    private String caseComputer;
    private String coolers;
}
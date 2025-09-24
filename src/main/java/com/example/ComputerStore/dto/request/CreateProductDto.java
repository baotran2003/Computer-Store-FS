package com.example.ComputerStore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateProductDto {
    
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    
    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0")
    private BigDecimal price;
    
    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    private String description;
    
    @NotNull(message = "Hình ảnh không được để trống")
    private List<String> images;
    
    @NotNull(message = "Category không được để trống")
    private UUID categoryId;
    
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer stock;
    
    @NotNull(message = "Loại sản phẩm không được để trống")
    private ComponentType componentType; // ComponentType enum
    
    @Builder.Default
    private Integer discount = 0; // Phần trăm giảm giá
    
    // Các thông số kỹ thuật cho PC hoàn chính (chỉ dùng khi componentType = "pc")
    private String cpu;
    private String main; // Mainboard
    private String ram;
    private String storage;
    private String gpu;
    private String power; // PSU
    private String caseComputer;
    private String coolers;
}
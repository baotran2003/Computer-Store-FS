package com.example.ComputerStore.dto.response;

import com.example.ComputerStore.enumeric.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterResponseDto {

    private List<ProductSummaryDto> products;
    private List<FilterOptionDto> filters;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductSummaryDto {
        private UUID id;
        private String name;
        private BigDecimal price;
        private List<String> images;
        private ComponentType componentType;
        private String cpu;
        private String ram;
        private String gpu;
        private String storage;
        private String coolers;
        private String power;
        private String caseComputer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FilterOptionDto {
        private ComponentType type;
        private String label;
        private List<FilterComponentDto> components;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FilterComponentDto {
        private String id;
        private String name;
        private ComponentType type;
        private UUID productId;
    }
}
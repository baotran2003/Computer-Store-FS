package com.example.ComputerStore.dto.response;

import com.example.ComputerStore.enumeric.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComponentTypeResponseDto {

    private ComponentType type;
    private String label;
    private List<ComponentDto> components;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComponentDto {
        private String id;
        private String name;
        private ComponentType type;
        private UUID productId;
    }
}
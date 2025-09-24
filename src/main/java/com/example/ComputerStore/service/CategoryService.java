package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.request.CreateCategoryDto;
import com.example.ComputerStore.dto.request.UpdateCategoryDto;
import com.example.ComputerStore.dto.response.CategoryResponseDto;
import com.example.ComputerStore.dto.response.ComponentTypeResponseDto;
import com.example.ComputerStore.dto.response.ProductFilterResponseDto;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    /**
     * Tạo danh mục mới
     */
    CategoryResponseDto createCategory(CreateCategoryDto createCategoryDto);

    /**
     * Lấy tất cả danh mục
     */
    List<CategoryResponseDto> getAllCategories();

    /**
     * Lấy danh mục theo ID
     */
    CategoryResponseDto getCategoryById(UUID categoryId);

    /**
     * Cập nhật danh mục
     */
    CategoryResponseDto updateCategory(UpdateCategoryDto updateCategoryDto);

    /**
     * Xóa danh mục
     */
    void deleteCategory(UUID categoryId);

    /**
     * Lấy components theo danh mục
     */
    List<ComponentTypeResponseDto> getCategoryByComponentTypes(UUID categoryId);

    /**
     * Lấy tất cả sản phẩm với filter options
     */
    ProductFilterResponseDto getAllProductsWithFilters(
        UUID categoryId,
        String componentType,
        String cpu,
        String ram,
        String gpu,
        String storage,
        String coolers,
        String power,
        String caseComputer
    );

    /**
     * Kiểm tra danh mục tồn tại
     */
    boolean categoryExists(UUID categoryId);

    /**
     * Lấy số lượng sản phẩm trong danh mục
     */
    long getProductCountInCategory(UUID categoryId);
}
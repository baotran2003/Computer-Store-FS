package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.CreateCategoryDto;
import com.example.ComputerStore.dto.request.UpdateCategoryDto;
import com.example.ComputerStore.dto.response.CategoryResponseDto;
import com.example.ComputerStore.dto.response.ComponentTypeResponseDto;
import com.example.ComputerStore.dto.response.ProductFilterResponseDto;
import com.example.ComputerStore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Tạo danh mục mới
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto) {
        log.info("Creating new category: {}", createCategoryDto.getName());
        
        try {
            CategoryResponseDto response = categoryService.createCategory(createCategoryDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Error creating category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy tất cả danh mục
     * GET /api/categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        log.info("Getting all categories");
        
        try {
            List<CategoryResponseDto> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (RuntimeException e) {
            log.error("Error getting categories: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy danh mục theo ID
     * GET /api/categories/{categoryId}
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable UUID categoryId) {
        log.info("Getting category by ID: {}", categoryId);
        
        try {
            CategoryResponseDto category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            log.error("Error getting category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Cập nhật danh mục
     * PUT /api/categories
     */
    @PutMapping
    public ResponseEntity<CategoryResponseDto> updateCategory(@Valid @RequestBody UpdateCategoryDto updateCategoryDto) {
        log.info("Updating category: {}", updateCategoryDto.getId());
        
        try {
            CategoryResponseDto response = categoryService.updateCategory(updateCategoryDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Xóa danh mục
     * DELETE /api/categories/{categoryId}
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {
        log.info("Deleting category: {}", categoryId);
        
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy components theo danh mục
     * GET /api/categories/component-types?categoryId={categoryId}
     */
    @GetMapping("/component-types")
    public ResponseEntity<List<ComponentTypeResponseDto>> getCategoryByComponentTypes(
            @RequestParam(required = false) UUID categoryId) {
        
        log.info("Getting component types for category: {}", categoryId);
        
        try {
            List<ComponentTypeResponseDto> components = categoryService.getCategoryByComponentTypes(categoryId);
            return ResponseEntity.ok(components);
        } catch (RuntimeException e) {
            log.error("Error getting component types: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy tất cả sản phẩm với filter options
     * GET /api/categories/products?categoryId={categoryId}&componentType={type}&cpu={cpu}...
     */
    @GetMapping("/products")
    public ResponseEntity<ProductFilterResponseDto> getAllProductsWithFilters(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String componentType,
            @RequestParam(required = false) String cpu,
            @RequestParam(required = false) String ram,
            @RequestParam(required = false) String gpu,
            @RequestParam(required = false) String storage,
            @RequestParam(required = false) String coolers,
            @RequestParam(required = false) String power,
            @RequestParam(required = false) String caseComputer) {
        
        log.info("Getting products with filters for category: {}", categoryId);
        
        try {
            ProductFilterResponseDto response = categoryService.getAllProductsWithFilters(
                categoryId, componentType, cpu, ram, gpu, storage, coolers, power, caseComputer);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error getting products with filters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Kiểm tra danh mục tồn tại
     * GET /api/categories/{categoryId}/exists
     */
    @GetMapping("/{categoryId}/exists")
    public ResponseEntity<Boolean> categoryExists(@PathVariable UUID categoryId) {
        log.info("Checking if category exists: {}", categoryId);
        
        try {
            boolean exists = categoryService.categoryExists(categoryId);
            return ResponseEntity.ok(exists);
        } catch (RuntimeException e) {
            log.error("Error checking category existence: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lấy số lượng sản phẩm trong danh mục
     * GET /api/categories/{categoryId}/product-count
     */
    @GetMapping("/{categoryId}/product-count")
    public ResponseEntity<Long> getProductCountInCategory(@PathVariable UUID categoryId) {
        log.info("Getting product count for category: {}", categoryId);
        
        try {
            long count = categoryService.getProductCountInCategory(categoryId);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            log.error("Error getting product count: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
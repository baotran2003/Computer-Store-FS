package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.CreateProductDto;
import com.example.ComputerStore.dto.request.UpdateProductDto;
import com.example.ComputerStore.dto.response.ApiResponse;
import com.example.ComputerStore.dto.response.ProductResponseDto;
import com.example.ComputerStore.enumeric.ComponentType;
import com.example.ComputerStore.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * Create new product (Admin only)
     * POST /api/create-product
     */
    @PostMapping("/create-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(
            @Valid @RequestBody CreateProductDto createProductDto) {
        
        log.info("Creating product: {}", createProductDto.getName());
        ProductResponseDto product = productService.createProduct(createProductDto);
        
        return ResponseEntity.ok(ApiResponse.success("Create product successfully", product));
    }
    
    /**
     * Get all products
     * GET /api/get-products
     */
    @GetMapping("/get-products")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProducts() {
        
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Get products successfully", products));
    }
    
    /**
     * Update product (Admin only)  
     * POST /api/update-product
     */
    @PostMapping("/update-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @RequestParam UUID id,
            @Valid @RequestBody UpdateProductDto updateProductDto) {
        
        log.info("Updating product with ID: {}", id);
        ProductResponseDto product = productService.updateProduct(id, updateProductDto);
        
        return ResponseEntity.ok(ApiResponse.success("Update product successfully", product));
    }
    
    /**
     * Delete product (Admin only)
     * DELETE /api/delete-product
     */
    @DeleteMapping("/delete-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@RequestParam UUID id) {
        
        log.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        
        return ResponseEntity.ok(ApiResponse.success("Delete product successfully"));
    }
    
    /**
     * Get products grouped by categories
     * GET /api/get-products-by-categories
     */
    @GetMapping("/get-products-by-categories")
    public ResponseEntity<ApiResponse<List<Object>>> getProductsByCategories() {
        
        List<Object> result = productService.getProductsByCategories();
        return ResponseEntity.ok(ApiResponse.success(
                "Get all products grouped by categories successfully", result));
    }
    
    /**
     * Get product by ID
     * GET /api/get-product-by-id
     */
    @GetMapping("/get-product-by-id")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@RequestParam UUID id) {
        
        log.info("Getting product with ID: {}", id);
        ProductResponseDto product = productService.getProductById(id);
        
        // TODO: Add product reviews/previews like NodeJS version
        return ResponseEntity.ok(ApiResponse.success("Get product by id successfully", product));
    }
    
    /**
     * Get products by component type
     * GET /api/get-product-by-component-type
     */
    @GetMapping("/get-product-by-component-type")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProductByComponentType(
            @RequestParam String componentType) {
        
        try {
            ComponentType type = ComponentType.valueOf(componentType.toUpperCase());
            List<ProductResponseDto> products = productService.getProductsByComponentType(type);
            
            return ResponseEntity.ok(ApiResponse.success(
                    "Get product by component type successfully", products));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Invalid component type: " + componentType));
        }
    }
    
    /**
     * Get products by category ID with filters
     * GET /api/get-product-by-id-category
     */
    @GetMapping("/get-product-by-id-category")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProductByIdCategory(
            @RequestParam UUID id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String productIds) {
        
        // Parse productIds string to List<UUID>
        List<UUID> productIdList = null;
        if (productIds != null && !productIds.trim().isEmpty()) {
            try {
                productIdList = Arrays.stream(productIds.split(","))
                        .map(String::trim)
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Invalid product IDs format"));
            }
        }
        
        List<ProductResponseDto> products = productService.getProductsByCategory(
                id, search, minPrice, maxPrice, sort, productIdList);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Get product by id category successfully", products));
    }
    
    /**
     * Get hot sale products (discount > 20%)
     * GET /api/get-product-hot-sale
     */
    @GetMapping("/get-product-hot-sale")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProductHotSale() {
        
        List<ProductResponseDto> products = productService.getHotSaleProducts();
        return ResponseEntity.ok(ApiResponse.success(
                "Get product hot sale successfully", products));
    }
    
    /**
     * Search products with filters
     * GET /api/get-product-search
     */
    @GetMapping("/get-product-search")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProductSearch(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String productIds) {
        
        // Parse productIds
        List<UUID> productIdList = null;
        if (productIds != null && !productIds.trim().isEmpty()) {
            try {
                productIdList = Arrays.stream(productIds.split(","))
                        .map(String::trim)
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Invalid product IDs format"));
            }
        }
        
        List<ProductResponseDto> products = productService.searchProducts(
                search, minPrice, maxPrice, sort, productIdList);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Get product search successfully", products));
    }
    
    /**
     * Search products by category with filters
     * GET /api/get-product-search-by-category
     */
    @GetMapping("/get-product-search-by-category")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProductSearchByCategory(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String productIds,
            @RequestParam(required = false) String componentType) {
        
        // Parse category ID (handle "all" case from NodeJS)
        UUID categoryId = null;
        if (category != null && !category.equals("all")) {
            try {
                categoryId = UUID.fromString(category);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Invalid category ID format"));
            }
        }
        
        // Parse componentType
        ComponentType componentTypeEnum = null;
        if (componentType != null && !componentType.trim().isEmpty()) {
            try {
                componentTypeEnum = ComponentType.valueOf(componentType.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Invalid component type: " + componentType));
            }
        }
        
        // Parse productIds
        List<UUID> productIdList = null;
        if (productIds != null && !productIds.trim().isEmpty()) {
            try {
                productIdList = Arrays.stream(productIds.split(","))
                        .map(String::trim)
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Invalid product IDs format"));
            }
        }
        
        List<ProductResponseDto> products = productService.searchProductsByCategory(
                categoryId, search, minPrice, maxPrice, sort, productIdList, componentTypeEnum);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Get product search by category successfully", products));
    }


}
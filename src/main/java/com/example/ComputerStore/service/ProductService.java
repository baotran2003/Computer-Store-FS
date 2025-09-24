package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.request.CreateProductDto;
import com.example.ComputerStore.dto.request.UpdateProductDto;
import com.example.ComputerStore.dto.response.ProductResponseDto;
import com.example.ComputerStore.enumeric.ComponentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    
    /**
     * Create new product
     */
    ProductResponseDto createProduct(CreateProductDto createProductDto);
    
    /**
     * Get all products with pagination
     */
    Page<ProductResponseDto> getAllProducts(Pageable pageable);
    
    /**
     * Get all products without pagination (for compatibility with frontend)
     */
    List<ProductResponseDto> getAllProducts();
    
    /**
     * Get product by ID
     */
    ProductResponseDto getProductById(UUID id);
    
    /**
     * Update product
     */
    ProductResponseDto updateProduct(UUID id, UpdateProductDto updateProductDto);
    
    /**
     * Delete product
     */
    void deleteProduct(UUID id);
    
    /**
     * Get products grouped by categories
     */
    List<Object> getProductsByCategories();
    
    /**
     * Get products by component type
     */
    List<ProductResponseDto> getProductsByComponentType(ComponentType componentType);
    
    /**
     * Get products by category ID with filters
     */
    List<ProductResponseDto> getProductsByCategory(
            UUID categoryId, 
            String search, 
            BigDecimal minPrice, 
            BigDecimal maxPrice, 
            String sort,
            List<UUID> productIds
    );
    
    /**
     * Get hot sale products (discount > 20%)
     */
    List<ProductResponseDto> getHotSaleProducts();
    
    /**
     * Search products with filters
     */
    List<ProductResponseDto> searchProducts(
            String search, 
            BigDecimal minPrice, 
            BigDecimal maxPrice, 
            String sort,
            List<UUID> productIds
    );
    
    /**
     * Search products by category with filters
     */
    List<ProductResponseDto> searchProductsByCategory(
            UUID categoryId,
            String search, 
            BigDecimal minPrice, 
            BigDecimal maxPrice, 
            String sort,
            List<UUID> productIds,
            ComponentType componentType
    );
}
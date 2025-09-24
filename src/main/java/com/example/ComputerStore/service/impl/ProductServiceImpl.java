package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.request.CreateProductDto;
import com.example.ComputerStore.dto.request.UpdateProductDto;
import com.example.ComputerStore.dto.response.ProductResponseDto;
import com.example.ComputerStore.entity.Category;
import com.example.ComputerStore.entity.Product;
import com.example.ComputerStore.enumeric.ComponentType;
import com.example.ComputerStore.repository.CategoryRepository;
import com.example.ComputerStore.repository.ProductRepository;
import com.example.ComputerStore.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    @Override
    @Transactional
    public ProductResponseDto createProduct(CreateProductDto createProductDto) {
        log.info("Creating product with name: {}, componentType: {}", 
                createProductDto.getName(), createProductDto.getComponentType());
        
        // Validate category exists
        Category category = categoryRepository.findById(createProductDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Build product entity
        Product.ProductBuilder productBuilder = Product.builder()
                .name(createProductDto.getName())
                .price(createProductDto.getPrice())
                .description(createProductDto.getDescription())
                .images(String.join(",", createProductDto.getImages())) // Convert list to string
                .category(category)
                .stock(createProductDto.getStock())
                .componentType(createProductDto.getComponentType())
                .discount(createProductDto.getDiscount() != null ? 
                         BigDecimal.valueOf(createProductDto.getDiscount()) : BigDecimal.ZERO);
        
        // Add PC specifications if componentType is PC
        if (createProductDto.getComponentType() == ComponentType.PC) {
            productBuilder
                    .cpu(createProductDto.getCpu())
                    .main(createProductDto.getMain())
                    .ram(createProductDto.getRam())
                    .storage(createProductDto.getStorage())
                    .gpu(createProductDto.getGpu())
                    .power(createProductDto.getPower())
                    .caseComputer(createProductDto.getCaseComputer())
                    .coolers(createProductDto.getCoolers());
        }
        
        Product savedProduct = productRepository.save(productBuilder.build());
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        
        return convertToResponseDto(savedProduct);
    }
    
    @Override
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponseDto> productDtos = products.getContent().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(productDtos, pageable, products.getTotalElements());
    }
    
    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAllByOrderByCreatedAtDesc();
        return products.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProductResponseDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        return convertToResponseDto(product);
    }
    
    @Override
    @Transactional
    public ProductResponseDto updateProduct(UUID id, UpdateProductDto updateProductDto) {
        log.info("Updating product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Update basic fields
        if (updateProductDto.getName() != null) {
            product.setName(updateProductDto.getName());
        }
        if (updateProductDto.getPrice() != null) {
            product.setPrice(updateProductDto.getPrice());
        }
        if (updateProductDto.getDescription() != null) {
            product.setDescription(updateProductDto.getDescription());
        }
        if (updateProductDto.getImages() != null) {
            product.setImages(String.join(",", updateProductDto.getImages()));
        }
        if (updateProductDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateProductDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }
        if (updateProductDto.getStock() != null) {
            product.setStock(updateProductDto.getStock());
        }
        if (updateProductDto.getComponentType() != null) {
            product.setComponentType(updateProductDto.getComponentType());
        }
        if (updateProductDto.getDiscount() != null) {
            product.setDiscount(BigDecimal.valueOf(updateProductDto.getDiscount()));
        }
        
        // Update PC specifications
        if (updateProductDto.getCpu() != null) product.setCpu(updateProductDto.getCpu());
        if (updateProductDto.getMain() != null) product.setMain(updateProductDto.getMain());
        if (updateProductDto.getRam() != null) product.setRam(updateProductDto.getRam());
        if (updateProductDto.getStorage() != null) product.setStorage(updateProductDto.getStorage());
        if (updateProductDto.getGpu() != null) product.setGpu(updateProductDto.getGpu());
        if (updateProductDto.getPower() != null) product.setPower(updateProductDto.getPower());
        if (updateProductDto.getCaseComputer() != null) product.setCaseComputer(updateProductDto.getCaseComputer());
        if (updateProductDto.getCoolers() != null) product.setCoolers(updateProductDto.getCoolers());
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully");
        
        return convertToResponseDto(updatedProduct);
    }
    
    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        log.info("Deleting product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        productRepository.delete(product);
        log.info("Product deleted successfully");
    }
    
    @Override
    public List<Object> getProductsByCategories() {
        List<Category> categories = categoryRepository.findAll();
        
        return categories.stream()
                .map(category -> {
                    List<Product> products = productRepository.findByCategoryId(category.getId());
                    List<ProductResponseDto> productDtos = products.stream()
                            .map(this::convertToResponseDto)
                            .collect(Collectors.toList());
                    
                    Map<String, Object> categoryData = new HashMap<>();
                    categoryData.put("category", Map.of(
                            "id", category.getId(),
                            "name", category.getName()
                    ));
                    categoryData.put("products", productDtos);
                    
                    return categoryData;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponseDto> getProductsByComponentType(ComponentType componentType) {
        List<Product> products = productRepository.findByComponentType(componentType);
        return products.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponseDto> getProductsByCategory(UUID categoryId, String search, 
                                                         BigDecimal minPrice, BigDecimal maxPrice, 
                                                         String sort, List<UUID> productIds) {
        
        List<Product> products = productRepository.searchByCategoryWithFilters(
                categoryId, null, search, minPrice, maxPrice, productIds);
        
        return applySortingAndConvert(products, sort);
    }
    
    @Override
    public List<ProductResponseDto> getHotSaleProducts() {
        List<Product> products = productRepository.findHotSaleProductsByDiscount(20);
        return products.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductResponseDto> searchProducts(String search, BigDecimal minPrice, 
                                                  BigDecimal maxPrice, String sort, 
                                                  List<UUID> productIds) {
        
        List<Product> products = productRepository.searchWithFilters(search, minPrice, maxPrice, productIds);
        return applySortingAndConvert(products, sort);
    }
    
    @Override
    public List<ProductResponseDto> searchProductsByCategory(UUID categoryId, String search, 
                                                           BigDecimal minPrice, BigDecimal maxPrice, 
                                                           String sort, List<UUID> productIds, 
                                                           ComponentType componentType) {
        
        List<Product> products = productRepository.searchByCategoryWithFilters(
                categoryId, componentType, search, minPrice, maxPrice, productIds);
        
        return applySortingAndConvert(products, sort);
    }
    
    /**
     * Apply sorting and convert to DTOs
     */
    private List<ProductResponseDto> applySortingAndConvert(List<Product> products, String sort) {
        List<ProductResponseDto> productDtos = products.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
        
        if (sort != null) {
            switch (sort) {
                case "price-asc":
                    productDtos.sort(Comparator.comparing(ProductResponseDto::getFinalPrice));
                    break;
                case "price-desc":
                    productDtos.sort(Comparator.comparing(ProductResponseDto::getFinalPrice).reversed());
                    break;
                case "name-asc":
                    productDtos.sort(Comparator.comparing(ProductResponseDto::getName));
                    break;
                case "name-desc":
                    productDtos.sort(Comparator.comparing(ProductResponseDto::getName).reversed());
                    break;
                case "newest":
                    productDtos.sort(Comparator.comparing(ProductResponseDto::getCreatedAt).reversed());
                    break;
                default:
                    // No sorting or unknown sort type
                    break;
            }
        }
        
        return productDtos;
    }
    
    /**
     * Convert Product entity to ProductResponseDto
     */
    private ProductResponseDto convertToResponseDto(Product product) {
        // Calculate final price after discount
        BigDecimal finalPrice = product.getPrice();
        if (product.getDiscount() != null && product.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = product.getPrice()
                    .multiply(product.getDiscount())
                    .divide(BigDecimal.valueOf(100));
            finalPrice = product.getPrice().subtract(discountAmount);
        }
        
        // Parse images from string to list
        List<String> imagesList = product.getImages() != null ? 
                Arrays.asList(product.getImages().split(",")) : new ArrayList<>();
        
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .finalPrice(finalPrice)
                .description(product.getDescription())
                .images(imagesList)
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .stock(product.getStock())
                .componentType(product.getComponentType())
                .discount(product.getDiscount() != null ? product.getDiscount().intValue() : 0)
                .cpu(product.getCpu())
                .main(product.getMain())
                .ram(product.getRam())
                .storage(product.getStorage())
                .gpu(product.getGpu())
                .power(product.getPower())
                .caseComputer(product.getCaseComputer())
                .coolers(product.getCoolers())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                // TODO: Add review statistics calculation
                .averageRating(0.0)
                .totalReviews(0)
                .build();
    }
}
package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.request.CreateCategoryDto;
import com.example.ComputerStore.dto.request.UpdateCategoryDto;
import com.example.ComputerStore.dto.response.CategoryResponseDto;
import com.example.ComputerStore.dto.response.ComponentTypeResponseDto;
import com.example.ComputerStore.dto.response.ProductFilterResponseDto;
import com.example.ComputerStore.entity.Category;
import com.example.ComputerStore.entity.Product;
import com.example.ComputerStore.enumeric.ComponentType;
import com.example.ComputerStore.repository.CategoryRepository;
import com.example.ComputerStore.repository.ProductRepository;
import com.example.ComputerStore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public CategoryResponseDto createCategory(CreateCategoryDto createCategoryDto) {
        log.info("Creating new category: {}", createCategoryDto.getName());
        
        // Kiểm tra tên danh mục đã tồn tại
        if (categoryRepository.existsByName(createCategoryDto.getName())) {
            throw new RuntimeException("Danh mục với tên '" + createCategoryDto.getName() + "' đã tồn tại");
        }
        
        // Tạo category mới
        Category category = Category.builder()
            .name(createCategoryDto.getName())
            .image(createCategoryDto.getImage())
            .build();
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        
        return convertToCategoryResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        log.info("Getting all categories");
        
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        
        return categories.stream()
            .map(this::convertToCategoryResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(UUID categoryId) {
        log.info("Getting category by ID: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với ID: " + categoryId));
        
        return convertToCategoryResponse(category);
    }

    @Override
    public CategoryResponseDto updateCategory(UpdateCategoryDto updateCategoryDto) {
        log.info("Updating category with ID: {}", updateCategoryDto.getId());
        
        Category category = categoryRepository.findById(updateCategoryDto.getId())
            .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với ID: " + updateCategoryDto.getId()));
        
        // Kiểm tra tên mới không trùng với category khác
        Optional<Category> existingCategory = categoryRepository.findByName(updateCategoryDto.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(updateCategoryDto.getId())) {
            throw new RuntimeException("Danh mục với tên '" + updateCategoryDto.getName() + "' đã tồn tại");
        }
        
        // Cập nhật thông tin
        category.setName(updateCategoryDto.getName());
        category.setImage(updateCategoryDto.getImage());
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully: {}", updatedCategory.getId());
        
        return convertToCategoryResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        log.info("Deleting category with ID: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với ID: " + categoryId));
        
        // Kiểm tra có sản phẩm trong danh mục không
        long productCount = categoryRepository.countProductsInCategory(categoryId);
        if (productCount > 0) {
            throw new RuntimeException("Không thể xóa danh mục vì còn " + productCount + " sản phẩm trong danh mục này");
        }
        
        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComponentTypeResponseDto> getCategoryByComponentTypes(UUID categoryId) {
        log.info("Getting component types for category: {}", categoryId);
        
        // Lấy sản phẩm theo category (nếu có)
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAll();
        }
        
        // Map để lưu components theo type
        Map<ComponentType, Map<String, ComponentTypeResponseDto.ComponentDto>> componentGroups = new HashMap<>();
        
        // Khởi tạo cho tất cả component types
        for (ComponentType type : ComponentType.values()) {
            componentGroups.put(type, new HashMap<>());
        }
        
        // Phân tích từng sản phẩm
        products.forEach(product -> {
            // Xử lý CPU
            if (product.getCpu() != null && !product.getCpu().trim().isEmpty()) {
                addComponent(componentGroups, ComponentType.CPU, product.getCpu(), product.getId());
            }
            
            // Xử lý RAM
            if (product.getRam() != null && !product.getRam().trim().isEmpty()) {
                addComponent(componentGroups, ComponentType.RAM, product.getRam(), product.getId());
            }
            
            // Xử lý GPU/VGA
            if (product.getGpu() != null && !product.getGpu().trim().isEmpty()) {
                addComponent(componentGroups, ComponentType.VGA, product.getGpu(), product.getId());
            }
            
            // Xử lý Storage
            if (product.getStorage() != null && !product.getStorage().trim().isEmpty()) {
                ComponentType storageType = product.getStorage().toLowerCase().contains("ssd") 
                    ? ComponentType.SSD : ComponentType.HDD;
                addComponent(componentGroups, storageType, product.getStorage(), product.getId());
            }
            
            // Xử lý Coolers
            if (product.getCoolers() != null && !product.getCoolers().trim().isEmpty()) {
                addComponent(componentGroups, ComponentType.COOLER, product.getCoolers(), product.getId());
            }
            
            // Xử lý Power
            if (product.getPower() != null && !product.getPower().trim().isEmpty()) {
                addComponent(componentGroups, ComponentType.POWER, product.getPower(), product.getId());
            }
            
            // Xử lý Case
            if (product.getCaseComputer() != null && !product.getCaseComputer().trim().isEmpty()) {
                addComponent(componentGroups, ComponentType.CASE, product.getCaseComputer(), product.getId());
            }
            
            // Xử lý theo ComponentType của sản phẩm
            if (product.getComponentType() != null && !ComponentType.PC.equals(product.getComponentType())) {
                addComponent(componentGroups, product.getComponentType(), product.getName(), product.getId());
            }
        });
        
        // Chuyển đổi thành response
        List<ComponentTypeResponseDto> result = new ArrayList<>();
        
        componentGroups.forEach((type, components) -> {
            if (!components.isEmpty()) {
                List<ComponentTypeResponseDto.ComponentDto> componentList = new ArrayList<>(components.values());
                
                ComponentTypeResponseDto responseDto = ComponentTypeResponseDto.builder()
                    .type(type)
                    .label(type.name().toLowerCase())
                    .components(componentList)
                    .build();
                
                result.add(responseDto);
            }
        });
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductFilterResponseDto getAllProductsWithFilters(
            UUID categoryId, String componentType, String cpu, String ram, 
            String gpu, String storage, String coolers, String power, String caseComputer) {
        
        log.info("Getting products with filters for category: {}", categoryId);
        
        // Lấy tất cả sản phẩm
        List<Product> allProducts = productRepository.findAllByOrderByCreatedAtDesc();
        
        // Convert products to summary DTOs
        List<ProductFilterResponseDto.ProductSummaryDto> productSummaries = allProducts.stream()
            .map(this::convertToProductSummary)
            .collect(Collectors.toList());
        
        // Tạo filter options từ tất cả sản phẩm
        List<ProductFilterResponseDto.FilterOptionDto> filters = createFilterOptions(allProducts);
        
        return ProductFilterResponseDto.builder()
            .products(productSummaries)
            .filters(filters)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean categoryExists(UUID categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getProductCountInCategory(UUID categoryId) {
        return categoryRepository.countProductsInCategory(categoryId);
    }
    
    // Helper methods
    private void addComponent(Map<ComponentType, Map<String, ComponentTypeResponseDto.ComponentDto>> componentGroups,
                             ComponentType type, String name, UUID productId) {
        String componentId = type.name().toLowerCase() + "-" + productId;
        
        ComponentTypeResponseDto.ComponentDto component = ComponentTypeResponseDto.ComponentDto.builder()
            .id(componentId)
            .name(name)
            .type(type)
            .productId(productId)
            .build();
        
        componentGroups.get(type).put(name, component);
    }
    
    private ProductFilterResponseDto.ProductSummaryDto convertToProductSummary(Product product) {
        List<String> images = (product.getImages() != null && !product.getImages().trim().isEmpty()) 
            ? Arrays.asList(product.getImages().split(","))
            : List.of();
        
        return ProductFilterResponseDto.ProductSummaryDto.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .images(images)
            .componentType(product.getComponentType())
            .cpu(product.getCpu())
            .ram(product.getRam())
            .gpu(product.getGpu())
            .storage(product.getStorage())
            .coolers(product.getCoolers())
            .power(product.getPower())
            .caseComputer(product.getCaseComputer())
            .build();
    }
    
    private List<ProductFilterResponseDto.FilterOptionDto> createFilterOptions(List<Product> products) {
        Map<ComponentType, Map<String, ProductFilterResponseDto.FilterComponentDto>> filterGroups = new HashMap<>();
        
        // Khởi tạo cho tất cả component types
        for (ComponentType type : ComponentType.values()) {
            filterGroups.put(type, new HashMap<>());
        }
        
        // Phân tích sản phẩm để tạo filter options
        products.forEach(product -> {
            if (product.getCpu() != null && !product.getCpu().trim().isEmpty()) {
                addFilterComponent(filterGroups, ComponentType.CPU, product.getCpu(), product.getId());
            }
            if (product.getRam() != null && !product.getRam().trim().isEmpty()) {
                addFilterComponent(filterGroups, ComponentType.RAM, product.getRam(), product.getId());
            }
            if (product.getGpu() != null && !product.getGpu().trim().isEmpty()) {
                addFilterComponent(filterGroups, ComponentType.VGA, product.getGpu(), product.getId());
            }
            if (product.getStorage() != null && !product.getStorage().trim().isEmpty()) {
                ComponentType storageType = product.getStorage().toLowerCase().contains("ssd") 
                    ? ComponentType.SSD : ComponentType.HDD;
                addFilterComponent(filterGroups, storageType, product.getStorage(), product.getId());
            }
            if (product.getCoolers() != null && !product.getCoolers().trim().isEmpty()) {
                addFilterComponent(filterGroups, ComponentType.COOLER, product.getCoolers(), product.getId());
            }
            if (product.getPower() != null && !product.getPower().trim().isEmpty()) {
                addFilterComponent(filterGroups, ComponentType.POWER, product.getPower(), product.getId());
            }
            if (product.getCaseComputer() != null && !product.getCaseComputer().trim().isEmpty()) {
                addFilterComponent(filterGroups, ComponentType.CASE, product.getCaseComputer(), product.getId());
            }
            
            // Component type của sản phẩm
            if (product.getComponentType() != null && !ComponentType.PC.equals(product.getComponentType())) {
                addFilterComponent(filterGroups, product.getComponentType(), product.getName(), product.getId());
            }
        });
        
        // Chuyển đổi thành filter options
        List<ProductFilterResponseDto.FilterOptionDto> result = new ArrayList<>();
        
        filterGroups.forEach((type, components) -> {
            if (!components.isEmpty()) {
                List<ProductFilterResponseDto.FilterComponentDto> componentList = new ArrayList<>(components.values());
                
                ProductFilterResponseDto.FilterOptionDto filterOption = ProductFilterResponseDto.FilterOptionDto.builder()
                    .type(type)
                    .label(type.name().toLowerCase())
                    .components(componentList)
                    .build();
                
                result.add(filterOption);
            }
        });
        
        return result;
    }
    
    private void addFilterComponent(Map<ComponentType, Map<String, ProductFilterResponseDto.FilterComponentDto>> filterGroups,
                                   ComponentType type, String name, UUID productId) {
        String componentId = type.name().toLowerCase() + "-" + productId;
        
        ProductFilterResponseDto.FilterComponentDto component = ProductFilterResponseDto.FilterComponentDto.builder()
            .id(componentId)
            .name(name)
            .type(type)
            .productId(productId)
            .build();
        
        filterGroups.get(type).put(name, component);
    }
    
    private CategoryResponseDto convertToCategoryResponse(Category category) {
        long productCount = categoryRepository.countProductsInCategory(category.getId());
        
        return CategoryResponseDto.builder()
            .id(category.getId())
            .name(category.getName())
            .image(category.getImage())
            .productCount(productCount)
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}
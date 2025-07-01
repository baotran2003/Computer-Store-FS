package com.example.ComputerStore.service;

import com.example.ComputerStore.entity.Category;
import com.example.ComputerStore.entity.Product;
import com.example.ComputerStore.exception.BadRequestError;
import com.example.ComputerStore.repository.CategoryRepository;
import com.example.ComputerStore.repository.ProductRepository;
import com.example.ComputerStore.service.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public Category createCategory(String name, String image) {
        if (name == null || image == null) {
            throw new BadRequestError("Tên và ảnh là bắt buộc");
        }
        Category category = Category.builder()
                .name(name)
                .image(image)
                .build();
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestError("Category not found"));
        categoryRepository.delete(category);
        return category;
    }

    public Category updateCategory(UUID id, String name, String image) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestError("Category not found"));

        category.setName(name);
        category.setImage(image);
        return categoryRepository.save(category);
    }

    public List<Map<String, Object>> getCategoryByComponentTypes(UUID categoryId) {
        List<Product> products;

        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAll();
        }

        Map<String, Map<String, List<Map<String, Object>>>> componentGroups = initComponentGroups();

        for (Product product : products) {
            if (product.getCpu() != null) {
                addComponent(componentGroups, "CPU", product.getCpu(), product);
            }
            if (product.getRam() != null) {
                addComponent(componentGroups, "RAM", product.getRam(), product);
            }
            if (product.getGpu() != null) {
                addComponent(componentGroups, "VGA", product.getGpu(), product);
            }
            if (product.getStorage() != null) {
                String storage = product.getStorage().toLowerCase();
                if (storage.contains("ssd")) {
                    addComponent(componentGroups, "SSD", product.getStorage(), product);
                } else if (storage.contains("hdd")) {
                    addComponent(componentGroups, "HDD", product.getStorage(), product);
                }
            }
            if (product.getCoolers() != null) {
                addComponent(componentGroups, "COOLER", product.getCoolers(), product);
            }
            if (product.getPower() != null) {
                addComponent(componentGroups, "POWER", product.getPower(), product);
            }
            if (product.getCaseComputer() != null) {
                addComponent(componentGroups, "CASE", product.getCaseComputer(), product);
            }
                if (product.getComponentType() != null ) {
                    String type = product.getComponentType().name().toLowerCase();
                    addComponent(componentGroups, type, product.getName(), product);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : componentGroups.entrySet()) {
            String type = entry.getKey();
            Map<String, List<Map<String, Object>>> components = entry.getValue();

            List<Map<String, Object>> typeComponents = new ArrayList<>();
            for (var compEntry : components.entrySet()) {
                for (var productItem : compEntry.getValue()) {
                    Map<String, Object> obj = new HashMap<>();
                    obj.put("id", type + "-" + productItem.get("id"));
                    obj.put("name", compEntry.getKey());
                    obj.put("type", type);
                    obj.put("productId", productItem.get("id"));
                    typeComponents.add(obj);
                }
            }

            if (!typeComponents.isEmpty()) {
                Map<String, Object> groupObj = new HashMap<>();
                groupObj.put("type", type);
                groupObj.put("label", type);
                groupObj.put("components", typeComponents);
                result.add(groupObj);
            }
        }

        return result;
    }

    private Map<String, Map<String, List<Map<String, Object>>>> initComponentGroups() {
        Map<String, Map<String, List<Map<String, Object>>>> groups = new HashMap<>();
        List<String> types = List.of(
                "CPU", "RAM", "VGA", "MAINBOARD", "SSD", "HDD",
                "POWER", "COOLER", "CASE", "MONITOR", "KEYBOARD", "MOUSE", "HEADSET"
        );
        for (String type : types) {
            groups.put(type, new HashMap<>());
        }
        return groups;
    }

    private void addComponent(Map<String, Map<String, List<Map<String, Object>>>> groups,
                              String type, String name, Product product) {
        groups.computeIfAbsent(type, k -> new HashMap<>());
        groups.get(type).computeIfAbsent(name, k -> new ArrayList<>());
        groups.get(type).get(name).add(Map.of(
                "id", product.getId(),
                "name", name
        ));
    }
}

package com.example.ComputerStore.service.service;

import com.example.ComputerStore.entity.Category;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CategoryService {
    Category createCategory(String name, String image);

    List<Category> getAllCategories();

    Category deleteCategory(UUID id);

    Category updateCategory(UUID id, String name, String image);

    List<Map<String, Object>> getCategoryByComponentTypes(UUID categoryId);
}

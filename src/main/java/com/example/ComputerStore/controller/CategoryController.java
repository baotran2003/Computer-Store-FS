package com.example.ComputerStore.controller;

import com.example.ComputerStore.entity.Category;
import com.example.ComputerStore.entity.payload.ApiResponse;
import com.example.ComputerStore.service.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")

public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String image = payload.get("image");
        Category category = categoryService.createCategory(name, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Category>builder()
                        .status(HttpStatus.CREATED.value())
                        .error(null)
                        .message("Create category successfully")
                        .data(category)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(
                ApiResponse.<List<Category>>builder()
                        .status(HttpStatus.OK.value())
                        .error(null)
                        .message("Get all category successfully")
                        .data(categories)
                        .build()
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Category>> deleteCategory(@RequestParam UUID id) {
        Category deleted = categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                ApiResponse.<Category>builder()
                        .status(HttpStatus.OK.value())
                        .error(null)
                        .message("Delete category successfully")
                        .data(deleted)
                        .build()
        );
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@RequestBody Map<String, String> payload) {
        UUID id = UUID.fromString(payload.get("id"));
        String name = payload.get("name");
        String image = payload.get("image");

        Category updated = categoryService.updateCategory(id, name, image);

        return ResponseEntity.ok(
                ApiResponse.<Category>builder()
                        .status(HttpStatus.OK.value())
                        .error(null)
                        .message("Update category successfully")
                        .data(updated)
                        .build()
        );
    }


}

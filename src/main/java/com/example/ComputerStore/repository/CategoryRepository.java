package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // tim category theo name
    Optional<Category> findByName(String name);

    // check name exists
    boolean existsByName(String name);

    // find category theo name chua tu khoa
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name%")
    List<Category> findByNameContaining(@Param("name") String name);

    // find category co products
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p")
    List<Category> findCategoriesWithProducts();

    // count products in category
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countProductsInCategory(@Param("categoryId") UUID categoryId);

    // find categories sap xep theo name
    List<Category> findAllByOrderByNameAsc();
}

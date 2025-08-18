package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    // Tim products theo category
    List<Product> findByCategoryId(UUID categoryId);
    Page<Product> findByCategoryId(UUID categoryId, Pageable pageable);

    // Tim products theo name
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    Page<Product> findByNameContaining(@Param("name") String name, Pageable pageable);

    // tim products theo price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // Tim products co stock > 0
    List<Product> findByStockGreaterThan(Integer stock);
    Page<Product> findByStockGreaterThan(Integer stock, Pageable pageable);

    // Tim products co discount
    @Query("SELECT p FROM Product p WHERE p.discount > 0")
    List<Product> findProductsWithDiscount();

    @Query("SELECT p FROM Product p WHERE p.discount > 0")
    Page<Product> findProductsWithDiscount(Pageable pageable);

    // Tìm products theo componentType
    List<Product> findByComponentType(String componentType);
    Page<Product> findByComponentType(String componentType, Pageable pageable);

    // Tìm products với search filters
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:componentType IS NULL OR p.componentType = :componentType)")
    Page<Product> findWithFilters(@Param("name") String name,
                                  @Param("categoryId") UUID categoryId,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("componentType") String componentType,
                                  Pageable pageable);

    // Top selling products (based on payment quantity)
    @Query("SELECT p FROM Product p LEFT JOIN p.payments pay " +
           "GROUP BY p ORDER BY COALESCE(SUM(pay.quantity), 0) DESC")
    List<Product> findTopSellingProducts(Pageable pageable);

    // Products mới nhất
    List<Product> findTop10ByOrderByCreatedAtDesc();

    // Count products by category
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") UUID categoryId);

    // Tìm PC builds (products có đầy đủ components)
    @Query("SELECT p FROM Product p WHERE " +
            "p.cpu IS NOT NULL AND p.main IS NOT NULL " +
            "AND p.ram IS NOT NULL AND p.storage IS NOT NULL " +
            "AND p.gpu IS NOT NULL")
    List<Product> findPcBuilds();

    // Tìm products theo component specs (cho PC build)
    List<Product> findByCpu(String cpu);
    List<Product> findByMain(String main);
    List<Product> findByRam(String ram);
    List<Product> findByStorage(String storage);
    List<Product> findByGpu(String gpu);
    List<Product> findByPower(String power);
    List<Product> findByCaseComputer(String caseComputer);
    List<Product> findByCoolers(String coolers);

    // ========== BỔ SUNG CHO NODE.JS FEATURES ==========
    
    // Get category stats for pie chart
    @Query("SELECT c.name, COUNT(p) FROM Product p JOIN p.category c GROUP BY c.name")
    List<Object[]> getCategoryStats();
    
    // Search products by multiple criteria (for advanced search)
    @Query("SELECT p FROM Product p WHERE " +
           "(:searchText IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchText, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:componentType IS NULL OR p.componentType = :componentType)")
    Page<Product> searchProducts(@Param("searchText") String searchText,
                                 @Param("categoryId") UUID categoryId,
                                 @Param("componentType") String componentType,
                                 Pageable pageable);
    
    // Hot sale products (high discount)
    @Query("SELECT p FROM Product p WHERE p.discount >= :minDiscount ORDER BY p.discount DESC")
    List<Product> findHotSaleProducts(@Param("minDiscount") BigDecimal minDiscount, Pageable pageable);
    
    // PC build compatible products by component type
    @Query("SELECT p FROM Product p WHERE p.componentType = :componentType AND p.stock > 0")
    List<Product> findPcBuildComponents(@Param("componentType") String componentType);
}

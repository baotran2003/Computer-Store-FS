package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.ProductPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductPreviewRepository extends JpaRepository<ProductPreview, UUID> {
    // Tìm reviews theo product
    List<ProductPreview> findByProductId(UUID productId);

    Page<ProductPreview> findByProductId(UUID productId, Pageable pageable);

    // Tìm reviews theo user
    List<ProductPreview> findByUserId(UUID userId);

    // Tìm review specific của user cho product
    Optional<ProductPreview> findByUserIdAndProductId(UUID userId, UUID productId);

    // Kiểm tra user đã review product chưa
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);

    // Tìm reviews theo rating
    List<ProductPreview> findByRating(Integer rating);

    List<ProductPreview> findByProductIdAndRating(UUID productId, Integer rating);

    // Tìm reviews theo rating range
    List<ProductPreview> findByRatingBetween(Integer minRating, Integer maxRating);

    List<ProductPreview> findByProductIdAndRatingBetween(UUID productId, Integer minRating, Integer maxRating);

    // Tính rating trung bình của product
    @Query("SELECT AVG(p.rating) FROM ProductPreview p WHERE p.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") UUID productId);

    // Đếm số reviews của product
    @Query("SELECT COUNT(p) FROM ProductPreview p WHERE p.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    // Đếm số reviews theo rating
    @Query("SELECT COUNT(p) FROM ProductPreview p WHERE p.product.id = :productId AND p.rating = :rating")
    long countByProductIdAndRating(@Param("productId") UUID productId, @Param("rating") Integer rating);

    // Top rated products
    @Query("SELECT p.product, AVG(p.rating) as avgRating FROM ProductPreview p " +
            "GROUP BY p.product ORDER BY avgRating DESC")
    List<Object[]> findTopRatedProducts(Pageable pageable);

    // Recent reviews
    List<ProductPreview> findTop10ByOrderByCreatedAtDesc();

    // Reviews với content tìm kiếm
    @Query("SELECT p FROM ProductPreview p WHERE p.content LIKE %:keyword%")
    List<ProductPreview> findByContentContaining(@Param("keyword") String keyword);
}

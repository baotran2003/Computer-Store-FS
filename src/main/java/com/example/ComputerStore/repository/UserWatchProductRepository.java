package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.UserWatchProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWatchProductRepository extends JpaRepository<UserWatchProduct, UUID> {
    // Tìm watched products theo user
    List<UserWatchProduct> findByUserId(UUID userId);

    // Tìm users watching a product
    List<UserWatchProduct> findByProductId(UUID productId);

    // Tìm specific watch record
    Optional<UserWatchProduct> findByUserIdAndProductId(UUID userId, UUID productId);

    // Kiểm tra user có watch product này không
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);

    // Đếm số products user đang watch
    @Query("SELECT COUNT(u) FROM UserWatchProduct u WHERE u.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

    // Đếm số users watching product
    @Query("SELECT COUNT(u) FROM UserWatchProduct u WHERE u.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    // Xóa watch record
    void deleteByUserIdAndProductId(UUID userId, UUID productId);

    // Xóa tất cả watches của user
    void deleteByUserId(UUID userId);

    // Xóa tất cả watches của product
    void deleteByProductId(UUID productId);

    // Tìm most watched products
    @Query("SELECT u.product, COUNT(u) as watchCount " +
            "FROM UserWatchProduct u " +
            "GROUP BY u.product " +
            "ORDER BY watchCount DESC")
    List<Object[]> findMostWatchedProducts();
}

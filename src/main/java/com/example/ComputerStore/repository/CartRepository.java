package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    
    // Tìm cart items theo user
    List<Cart> findByUserId(UUID userId);
    
    // Tìm cart item theo user và product
    Optional<Cart> findByUserIdAndProductId(UUID userId, UUID productId);
    
    // Kiểm tra product có trong cart của user
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);
    
    // Tính tổng tiền cart của user
    @Query("SELECT COALESCE(SUM(c.totalPrice), 0) FROM Cart c WHERE c.user.id = :userId")
    BigDecimal getTotalPriceByUserId(@Param("userId") UUID userId);
    
    // Đếm số items trong cart của user
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);
    
    // Tính tổng quantity trong cart của user
    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM Cart c WHERE c.user.id = :userId")
    int getTotalQuantityByUserId(@Param("userId") UUID userId);
    
    // Xóa tất cả cart items của user
    void deleteByUserId(UUID userId);
    
    // Tìm cart items theo product
    List<Cart> findByProductId(UUID productId);
    
    // Xóa cart items theo product
    void deleteByProductId(UUID productId);
}

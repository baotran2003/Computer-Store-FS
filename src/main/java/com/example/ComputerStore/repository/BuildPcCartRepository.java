package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.BuildPcCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuildPcCartRepository extends JpaRepository<BuildPcCart, UUID> {
    // Tim buildPCCart items theo user
    List<BuildPcCart> findByUserId(UUID userId);

    // Tim buildPCCart theo user and product
    Optional<BuildPcCart> findByUserIdAndProductId(UUID userId, UUID productId);

    // check product co trong build pcCart cua user
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);

    // Tinh tong tien build pc cart cua user
    @Query(
            "SELECT COALESCE(SUM(b.totalPrice), 0) " +
                    "FROM BuildPcCart b WHERE b.user.id = :userId")
    BigDecimal getTotalPriceByUserId(@Param("userId") UUID userId);

    // Count items trong build pc cart cua user
    @Query("SELECT COUNT(b) FROM BuildPcCart b " +
            "WHERE b.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

    // Tong quantity trong build pc cart cua user
    @Query("SELECT COALESCE(SUM(b.quantity), 0) FROM BuildPcCart b WHERE b.user.id = :userId")
    int getTotalQuantityByUserId(@Param("userId") UUID userId);

    // xoa tat ca build pc cart cua user
    void deleteByUserId(UUID userId);

    // tim build pc cart theo product
    List<BuildPcCart> findByProductId(UUID productId);

    // xoa build pc cart theo product
    void deleteByProductId(UUID productId);
}

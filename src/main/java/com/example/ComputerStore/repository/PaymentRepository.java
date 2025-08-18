package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.Payment;
import com.example.ComputerStore.enumeric.PaymentStatus;
import com.example.ComputerStore.enumeric.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    // Tim payment theo user
    List<Payment> findByUserId(UUID userId);

    Page<Payment> findByUserId(UUID userId, Pageable pageable);

    // Tim payment theo idPayment
    Optional<Payment> findByIdPayment(String idPayment);

    // Tim payments theo status
    List<Payment> findByStatus(PaymentStatus status);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    // Tim payments theo type
    List<Payment> findByTypePayment(PaymentType paymentType);

    // Tim payments theo user va status
    List<Payment> findByUserIdAndStatus(UUID userId, PaymentStatus status);

    // Tinh tong doanh thu
    @Query("SELECT COALESCE(SUM(p.totalPrice), 0) FROM Payment p " +
            "WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    // Tinh doanh thu theo khoang thoi gian
    @Query("SELECT COALESCE(SUM(p.totalPrice), 0) FROM Payment p " +
            "WHERE p.status = 'COMPLETED'"
            + "AND p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    // Count orders theo status
    @Query("SELECT COUNT(p) FROM Payment p " +
            "WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);

    // Tìm Payments theo product
    List<Payment> findByProductId(UUID productId);

    // Top customers (theo tổng tiền đã mua)
    @Query("SELECT p.user.id, p.user.fullName, SUM(p.totalPrice) as total " +
            "FROM Payment p " +
            "WHERE p.status = 'completed' " +
            "GROUP BY p.user.id, p.user.fullName " +
            "ORDER BY total DESC")
    List<Object[]> findTopCustomers(Pageable pageable);

    // Doanh thu theo tháng
    @Query("SELECT YEAR(p.createdAt), MONTH(p.createdAt), SUM(p.totalPrice) FROM Payment p " +
            "WHERE p.status = 'completed' " +
            "GROUP BY YEAR(p.createdAt), MONTH(p.createdAt) " +
            "ORDER BY YEAR(p.createdAt) DESC, MONTH(p.createdAt) DESC")
    List<Object[]> getMonthlyRevenue();

    // Products bán chạy nhất
    @Query("SELECT p.product.id, p.product.name, SUM(p.quantity) as totalSold " +
            "FROM Payment p " +
            "WHERE p.status = 'COMPLETED' " +
            "GROUP BY p.product.id, p.product.name " +
            "ORDER BY totalSold DESC")
    List<Object[]> getBestSellingProducts(Pageable pageable);

    // ========== CẦN BỔ SUNG CHO NODE.JS FEATURES ==========
    
    // Get recent orders (top 5)
    List<Payment> findTop5ByOrderByCreatedAtDesc();
    
    // Get order stats by date range
    @Query("SELECT DATE(p.createdAt) as date, COUNT(p) as count FROM Payment p " +
           "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(p.createdAt) ORDER BY date")
    List<Object[]> getOrderStatsByDateRange(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    // Get order status stats for pie chart
    @Query("SELECT p.status, COUNT(p) FROM Payment p GROUP BY p.status")
    List<Object[]> getOrderStatusStats();
}

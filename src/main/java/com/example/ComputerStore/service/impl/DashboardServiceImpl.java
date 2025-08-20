package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.response.ApiResponse;
import com.example.ComputerStore.entity.Category;
import com.example.ComputerStore.entity.Payment;
import com.example.ComputerStore.entity.Product;
import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.enumeric.PaymentStatus;
import com.example.ComputerStore.repository.BlogRepository;
import com.example.ComputerStore.repository.CategoryRepository;
import com.example.ComputerStore.repository.PaymentRepository;
import com.example.ComputerStore.repository.ProductRepository;
import com.example.ComputerStore.repository.UserRepository;
import com.example.ComputerStore.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BlogRepository blogRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Thống kê cơ bản
            long totalUsers = userRepository.count();
            long totalProducts = productRepository.count();
            long totalCategories = categoryRepository.count();
            long totalBlogs = blogRepository.count();
            long totalPayments = paymentRepository.count();
            
            stats.put("totalUsers", totalUsers);
            stats.put("totalProducts", totalProducts);
            stats.put("totalCategories", totalCategories);
            stats.put("totalBlogs", totalBlogs);
            stats.put("totalPayments", totalPayments);
            
            // Thống kê doanh thu từ payments
            List<Payment> allPayments = paymentRepository.findAll();
            BigDecimal totalRevenue = allPayments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .map(Payment::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            stats.put("totalRevenue", totalRevenue);
            
            // Thống kê payment theo trạng thái
            Map<String, Long> paymentStatusStats = new HashMap<>();
            for (PaymentStatus status : PaymentStatus.values()) {
                long count = paymentRepository.countByStatus(status);
                paymentStatusStats.put(status.name(), count);
            }
            stats.put("paymentStatusStats", paymentStatusStats);
            
            // Thống kê user theo admin role (manual count)
            List<User> allUsers = userRepository.findAll();
            long adminUsers = allUsers.stream().filter(u -> "1".equals(u.getIsAdmin())).count();
            long regularUsers = allUsers.stream().filter(u -> "0".equals(u.getIsAdmin())).count();
            
            Map<String, Long> userRoleStats = new HashMap<>();
            userRoleStats.put("ADMIN", adminUsers);
            userRoleStats.put("USER", regularUsers);
            stats.put("userRoleStats", userRoleStats);
            
            // Payment gần đây (latest 10)
            List<Payment> recentPayments = paymentRepository.findAll()
                    .stream()
                    .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                    .limit(10)
                    .toList();
            stats.put("recentPayments", recentPayments);
            
            // Users đăng ký gần đây (latest 10)
            List<User> recentUsers = userRepository.findAll()
                    .stream()
                    .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                    .limit(10)
                    .toList();
            stats.put("recentUsers", recentUsers);
            
            return ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Lấy thống kê dashboard thành công")
                    .data(stats)
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi khi lấy thống kê dashboard: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<Map<String, Object>> getRevenueStats(String period) {
        try {
            Map<String, Object> revenueStats = new HashMap<>();
            LocalDate endDate = LocalDate.now();
            LocalDate startDate;
            
            switch (period.toLowerCase()) {
                case "week":
                    startDate = getStartOfWeek();
                    break;
                case "month":
                    startDate = getStartOfMonth();
                    break;
                case "quarter":
                    startDate = getStartOfQuarter();
                    break;
                case "year":
                    startDate = getStartOfYear();
                    break;
                default:
                    startDate = getStartOfMonth(); // Default to current month
            }
            
            // Doanh thu theo khoảng thời gian (manual filtering)
            List<Payment> allPayments = paymentRepository.findAll();
            List<Payment> payments = allPayments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .filter(p -> {
                        LocalDate paymentDate = p.getCreatedAt().toLocalDate();
                        return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                    })
                    .toList();
            
            BigDecimal totalRevenue = payments.stream()
                    .map(Payment::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            revenueStats.put("totalRevenue", totalRevenue);
            revenueStats.put("period", period);
            revenueStats.put("startDate", startDate);
            revenueStats.put("endDate", endDate);
            revenueStats.put("totalPayments", payments.size());
            
            // Doanh thu trung bình mỗi đơn
            BigDecimal avgPaymentValue = payments.size() > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(payments.size()), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
            revenueStats.put("avgPaymentValue", avgPaymentValue);
            
            return ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Lấy thống kê doanh thu thành công")
                    .data(revenueStats)
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi khi lấy thống kê doanh thu: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> userStats = new HashMap<>();
            
            // Tổng số users
            long totalUsers = userRepository.count();
            userStats.put("totalUsers", totalUsers);
            
            // Users theo role (admin/user) - manual filtering
            List<User> allUsers = userRepository.findAll();
            long adminUsers = allUsers.stream().filter(u -> "1".equals(u.getIsAdmin())).count();
            long regularUsers = allUsers.stream().filter(u -> "0".equals(u.getIsAdmin())).count();
            
            Map<String, Long> roleStats = new HashMap<>();
            roleStats.put("ADMIN", adminUsers);
            roleStats.put("USER", regularUsers);
            userStats.put("roleStats", roleStats);
            
            // Users đăng ký trong tuần/tháng (reuse existing list)
            LocalDate startOfWeek = getStartOfWeek();
            LocalDate startOfMonth = getStartOfMonth();
            
            long usersThisWeek = allUsers.stream()
                    .filter(u -> u.getCreatedAt().toLocalDate().isAfter(startOfWeek.minusDays(1)))
                    .count();
            long usersThisMonth = allUsers.stream()
                    .filter(u -> u.getCreatedAt().toLocalDate().isAfter(startOfMonth.minusDays(1)))
                    .count();
            
            userStats.put("usersThisWeek", usersThisWeek);
            userStats.put("usersThisMonth", usersThisMonth);
            
            return ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Lấy thống kê users thành công")
                    .data(userStats)
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi khi lấy thống kê users: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<Map<String, Object>> getProductStats() {
        try {
            Map<String, Object> productStats = new HashMap<>();
            
            // Tổng số sản phẩm
            long totalProducts = productRepository.count();
            productStats.put("totalProducts", totalProducts);
            
            // Sản phẩm theo category (manual count)
            List<Category> categories = categoryRepository.findAll();
            List<Product> allProducts = productRepository.findAll();
            Map<String, Long> productsByCategory = new HashMap<>();
            for (Category category : categories) {
                long count = allProducts.stream()
                        .filter(p -> p.getCategory().getId().equals(category.getId()))
                        .count();
                productsByCategory.put(category.getName(), count);
            }
            productStats.put("productsByCategory", productsByCategory);
            
            // Sản phẩm mới (7 ngày qua) - manual filtering
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            List<Product> newProducts = allProducts.stream()
                    .filter(p -> p.getCreatedAt().isAfter(sevenDaysAgo))
                    .toList();
            productStats.put("newProducts", newProducts.size());
            productStats.put("newProductsList", newProducts);
            
            return ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Lấy thống kê sản phẩm thành công")
                    .data(productStats)
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi khi lấy thống kê sản phẩm: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<Map<String, Object>> getOrderStats() {
        try {
            Map<String, Object> paymentStats = new HashMap<>();
            
            // Tổng số payments
            long totalPayments = paymentRepository.count();
            paymentStats.put("totalPayments", totalPayments);
            
            // Payments theo trạng thái
            Map<String, Long> statusStats = new HashMap<>();
            for (PaymentStatus status : PaymentStatus.values()) {
                long count = paymentRepository.countByStatus(status);
                statusStats.put(status.name(), count);
            }
            paymentStats.put("statusStats", statusStats);
            
            // Payments hôm nay (manual filtering)
            LocalDate today = LocalDate.now();
            List<Payment> allPayments = paymentRepository.findAll();
            List<Payment> paymentsToday = allPayments.stream()
                    .filter(p -> p.getCreatedAt().toLocalDate().equals(today))
                    .toList();
            paymentStats.put("paymentsToday", paymentsToday.size());
            
            // Payments tuần này (manual filtering)
            LocalDate startOfWeek = getStartOfWeek();
            List<Payment> paymentsThisWeek = allPayments.stream()
                    .filter(p -> {
                        LocalDate paymentDate = p.getCreatedAt().toLocalDate();
                        return !paymentDate.isBefore(startOfWeek) && !paymentDate.isAfter(LocalDate.now());
                    })
                    .toList();
            paymentStats.put("paymentsThisWeek", paymentsThisWeek.size());
            
            // Payments tháng này (manual filtering)
            LocalDate startOfMonth = getStartOfMonth();
            List<Payment> paymentsThisMonth = allPayments.stream()
                    .filter(p -> {
                        LocalDate paymentDate = p.getCreatedAt().toLocalDate();
                        return !paymentDate.isBefore(startOfMonth) && !paymentDate.isAfter(LocalDate.now());
                    })
                    .toList();
            paymentStats.put("paymentsThisMonth", paymentsThisMonth.size());
            
            // Payments gần đây (manual sorting and limit)
            List<Payment> recentPayments = allPayments.stream()
                    .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                    .limit(20)
                    .toList();
            paymentStats.put("recentPayments", recentPayments);
            
            return ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Lấy thống kê đơn hàng thành công")
                    .data(paymentStats)
                    .build();
                    
        } catch (Exception e) {
            return ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Lỗi khi lấy thống kê đơn hàng: " + e.getMessage())
                    .build();
        }
    }

    // Helper methods for date calculations
    private LocalDate getStartOfWeek() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private LocalDate getStartOfMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }

    private LocalDate getStartOfQuarter() {
        LocalDate now = LocalDate.now();
        int currentQuarter = (now.getMonthValue() - 1) / 3;
        int startMonth = currentQuarter * 3 + 1;
        return LocalDate.of(now.getYear(), startMonth, 1);
    }

    private LocalDate getStartOfYear() {
        return LocalDate.now().withDayOfYear(1);
    }
}

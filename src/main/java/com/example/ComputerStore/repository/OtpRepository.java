package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.Otp;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {
    List<Otp> findByEmail(String email);

    Optional<Otp> findByEmailAndOtpCode(String email, String otpCode);

    // Tim OTP valid (chưa sử dụng và chưa hết hạn)
    @Query("SELECT o FROM Otp o " +
            "WHERE o.email = :email " +
            "AND o.otpCode = :otpCode " +
            "AND o.isUsed = false " +
            "AND o.expiresAt > :currentTime")
    Optional<Otp> findValidOtp(
            @Param("email") String email,
            @Param("otpCode") String otpCode,
            @Param("currentTime") LocalDateTime currentTime
    );

    // Tim OTP chưa sử dụng email
    List<Otp> findByEmailAndIsUsedFalse(String email);

    // Tim OTP dã sử dụng email
    List<Otp> findByEmailAndIsUsedTrue(String email);

    // Tim OTP đã hết hạn
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :currentTime")
    @Modifying
    @Transactional
    List<Otp> findExpiredOtps(@Param("currentTime") LocalDateTime currentTime);

    // Xóa OTP đã hết hạn
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :currentTime")
    @Modifying
    @Transactional
    void deleteExpiredOtps(@Param("currentTime") LocalDateTime currentTime);

    // Xóa OTP đã sử dụng
    void deleteByIsUsedTrue();

    // Xóa tất cả OTP của email
    @Modifying
    @Transactional
    @Query("DELETE FROM Otp o WHERE o.email = :email")
    void deleteByEmail(String email);

    // Count OTP theo email
    @Query("SELECT COUNT(o) FROM Otp o WHERE o.email = :email")
    long countByEmail(@Param("email") String email);

    // Count OTP chưa sử dụng theo email
    @Query("SELECT COUNT(o) FROM Otp o WHERE o.email = :email AND o.isUsed = false")
    long countUnusedByEmail(@Param("email") String email);
}

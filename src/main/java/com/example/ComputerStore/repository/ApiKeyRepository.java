package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    // Tìm api key theo public key
    Optional<ApiKey> findByPublicKey(String publicKey);

    // Tìm api key theo private key
    Optional<ApiKey> findByPrivateKey(String privateKey);

    // Tìm api key theo user
    Optional<ApiKey> findByUserId(UUID userId);

    // Kiểm tra public key có tồn tại
    boolean existsByPublicKey(String publicKey);

    // Kiểm tra private key có tồn tại
    boolean existsByPrivateKey(String privateKey);

    // Kiểm tra user đã có api key chưa
    boolean existsByUserId(UUID userId);

    // Xóa api key theo user
    void deleteByUserId(UUID userId);
}

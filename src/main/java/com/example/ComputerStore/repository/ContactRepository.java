package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Contact entity operations
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    /**
     * Find contacts by phone number
     */
    List<Contact> findByPhone(String phone);

    /**
     * Find contacts by full name containing (case insensitive)
     */
    List<Contact> findByFullNameContainingIgnoreCase(String fullName);

    /**
     * Find contacts created between dates
     */
    @Query("SELECT c FROM Contact c WHERE c.createdAt BETWEEN :startDate AND :endDate ORDER BY c.createdAt DESC")
    List<Contact> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find contacts by purchase intention (option1)
     */
    List<Contact> findByOption1ContainingIgnoreCase(String option1);

    /**
     * Find all contacts ordered by creation date (newest first)
     */
    @Query("SELECT c FROM Contact c ORDER BY c.createdAt DESC")
    List<Contact> findAllOrderByCreatedAtDesc();

    /**
     * Find all contacts with pagination, ordered by creation date
     */
    @Query("SELECT c FROM Contact c ORDER BY c.createdAt DESC")
    Page<Contact> findAllOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Count contacts created today
     */
    @Query("SELECT COUNT(c) FROM Contact c WHERE DATE(c.createdAt) = CURRENT_DATE")
    Long countTodayContacts();

    /**
     * Count contacts by purchase intention
     */
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.option1 LIKE %:intention%")
    Long countByPurchaseIntention(@Param("intention") String intention);

    /**
     * Get latest contacts (top 10)
     */
    List<Contact> findTop10ByOrderByCreatedAtDesc();

    /**
     * Count total contacts
     */
    @Query("SELECT COUNT(c) FROM Contact c")
    Long countTotalContacts();
}

package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    List<Contact> findByEmail(String email);

    List<Contact> findByPhone(String phone);

    List<Contact> findByIsRead(Boolean isRead);
    Page<Contact> findByIsRead(Boolean isRead, Pageable pageable);

    // tim unread contacts
    List<Contact> findByIsReadFalse();
    Page<Contact> findByIsReadFalse(Pageable pageable);

    // Tìm read contacts
    List<Contact> findByIsReadTrue();
    Page<Contact> findByIsReadTrue(Pageable pageable);

    // Tìm contacts theo name
    @Query("SELECT c FROM Contact c WHERE c.name LIKE %:name%")
    List<Contact> findByNameContaining(@Param("name") String name);

    // Tìm contacts theo message content
    @Query("SELECT c FROM Contact c WHERE c.message LIKE %:message%")
    List<Contact> findByMessageContaining(@Param("message") String message);

    // Count unread contacts
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.isRead = false")
    long countUnreadContacts();

    // Count total contacts
    @Query("SELECT COUNT(c) FROM Contact c")
    long countTotalContacts();

    // Latest contacts
    List<Contact> findTop10ByOrderByCreatedAtDesc();

    // All contacts ordered by created date
    Page<Contact> findAllByOrderByCreatedAtDesc(Pageable pageable);

}

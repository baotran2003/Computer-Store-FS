package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.request.CreateContactDto;
import com.example.ComputerStore.dto.response.ContactResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ContactService {

    ContactResponseDto createContact(CreateContactDto createContactDto);

    List<ContactResponseDto> getAllContacts();

    Page<ContactResponseDto> getAllContacts(Pageable pageable);

    ContactResponseDto getContactById(UUID id);

    void deleteContact(UUID id);

    List<ContactResponseDto> findContactsByPhone(String phone);

    List<ContactResponseDto> findContactsByFullName(String fullName);

    List<ContactResponseDto> getContactsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<ContactResponseDto> getContactsByPurchaseIntention(String intention);

    ContactStatisticsDto getContactStatistics();

    class ContactStatisticsDto {
        private Long totalContacts;
        private Long todayContacts;
        private Long intentionBuyContacts;
        
        // Constructors, getters, setters
        public ContactStatisticsDto() {}
        
        public ContactStatisticsDto(Long totalContacts, Long todayContacts, Long intentionBuyContacts) {
            this.totalContacts = totalContacts;
            this.todayContacts = todayContacts;
            this.intentionBuyContacts = intentionBuyContacts;
        }
        
        public Long getTotalContacts() { return totalContacts; }
        public void setTotalContacts(Long totalContacts) { this.totalContacts = totalContacts; }
        
        public Long getTodayContacts() { return todayContacts; }
        public void setTodayContacts(Long todayContacts) { this.todayContacts = todayContacts; }
        
        public Long getIntentionBuyContacts() { return intentionBuyContacts; }
        public void setIntentionBuyContacts(Long intentionBuyContacts) { this.intentionBuyContacts = intentionBuyContacts; }
    }
}
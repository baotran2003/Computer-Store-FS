package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.request.CreateContactDto;
import com.example.ComputerStore.dto.response.ContactResponseDto;
import com.example.ComputerStore.entity.Contact;
import com.example.ComputerStore.repository.ContactRepository;
import com.example.ComputerStore.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ContactService
 * Handles PC build consultation requests and contact management
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Override
    public ContactResponseDto createContact(CreateContactDto createContactDto) {
        log.info("Creating new contact for customer: {}", createContactDto.getFullName());
        
        try {
            // Build Contact entity from DTO
            Contact contact = Contact.builder()
                    .fullName(createContactDto.getFullName())
                    .phone(createContactDto.getPhone())
                    .email(createContactDto.getEmail()) 
                    .message(createContactDto.getMessage()) 
                    .option1(createContactDto.getOption1())
                    .option2(createContactDto.getOption2())
                    .option3(createContactDto.getOption3())
                    .option4(createContactDto.getOption4())
                    .build();

            // Save to database
            Contact savedContact = contactRepository.save(contact);
            
            log.info("Contact created successfully with ID: {}", savedContact.getId());
            
            // Convert to response DTO
            return convertToResponseDto(savedContact);
            
        } catch (Exception e) {
            log.error("Error creating contact for customer {}: ", createContactDto.getFullName(), e);
            throw new RuntimeException("Lỗi khi tạo liên hệ: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponseDto> getAllContacts() {
        log.info("Fetching all contacts");
        
        List<Contact> contacts = contactRepository.findAllOrderByCreatedAtDesc();
        
        return contacts.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactResponseDto> getAllContacts(Pageable pageable) {
        log.info("Fetching contacts with pagination: page {}, size {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Contact> contactPage = contactRepository.findAllOrderByCreatedAtDesc(pageable);
        
        return contactPage.map(this::convertToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponseDto getContactById(UUID id) {
        log.info("Fetching contact by ID: {}", id);
        
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy liên hệ với ID: " + id));
        
        return convertToResponseDto(contact);
    }

    @Override
    public void deleteContact(UUID id) {
        log.info("Deleting contact with ID: {}", id);
        
        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy liên hệ với ID: " + id);
        }
        
        contactRepository.deleteById(id);
        log.info("Contact deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponseDto> findContactsByPhone(String phone) {
        log.info("Searching contacts by phone: {}", phone);
        
        List<Contact> contacts = contactRepository.findByPhone(phone);
        
        return contacts.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponseDto> findContactsByFullName(String fullName) {
        log.info("Searching contacts by full name: {}", fullName);
        
        List<Contact> contacts = contactRepository.findByFullNameContainingIgnoreCase(fullName);
        
        return contacts.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponseDto> getContactsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching contacts between {} and {}", startDate, endDate);
        
        List<Contact> contacts = contactRepository.findByCreatedAtBetween(startDate, endDate);
        
        return contacts.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponseDto> getContactsByPurchaseIntention(String intention) {
        log.info("Fetching contacts by purchase intention: {}", intention);
        
        List<Contact> contacts = contactRepository.findByOption1ContainingIgnoreCase(intention);
        
        return contacts.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ContactStatisticsDto getContactStatistics() {
        log.info("Calculating contact statistics");
        
        Long totalContacts = contactRepository.countTotalContacts();
        Long todayContacts = contactRepository.countTodayContacts();
        Long intentionBuyContacts = contactRepository.countByPurchaseIntention("Có");
        
        return new ContactStatisticsDto(totalContacts, todayContacts, intentionBuyContacts);
    }

    /**
     * Convert Contact entity to ContactResponseDto
     */
    private ContactResponseDto convertToResponseDto(Contact contact) {
        return ContactResponseDto.builder()
                .id(contact.getId())
                .fullName(contact.getFullName())
                .phone(contact.getPhone())
                .email(contact.getEmail())
                .message(contact.getMessage())
                .option1(contact.getOption1())
                .option2(contact.getOption2())
                .option3(contact.getOption3())
                .option4(contact.getOption4())
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }
}
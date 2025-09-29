package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.CreateContactDto;
import com.example.ComputerStore.dto.response.ApiResponse;
import com.example.ComputerStore.dto.response.ContactResponseDto;
import com.example.ComputerStore.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Contact Management Controller
 * Handles PC build consultation requests and contact management
 */
@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contact Management", description = "PC Build Consultation API")
@Slf4j
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * Submit PC build consultation request
     * Public endpoint - no authentication required
     */
    @PostMapping
    @Operation(summary = "Submit consultation request", 
               description = "Submit PC build consultation request with customer requirements")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Contact created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ApiResponse<ContactResponseDto>> createContact(
            @Valid @RequestBody CreateContactDto createContactDto) {
        
        log.info("Received contact request from: {}", createContactDto.getFullName());
        
        try {
            ContactResponseDto contactResponse = contactService.createContact(createContactDto);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<ContactResponseDto>builder()
                            .success(true)
                            .message("Tạo liên hệ thành công")
                            .data(contactResponse)
                            .build());
                            
        } catch (Exception e) {
            log.error("Error creating contact: ", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<ContactResponseDto>builder()
                            .success(false)
                            .message("Lỗi khi tạo liên hệ: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get all consultation requests (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all contacts", 
               description = "Retrieve all PC build consultation requests (Admin only)",
               security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<ApiResponse<List<ContactResponseDto>>> getAllContacts() {
        
        log.info("Admin fetching all contacts");
        
        try {
            List<ContactResponseDto> contacts = contactService.getAllContacts();
            
            return ResponseEntity.ok(ApiResponse.<List<ContactResponseDto>>builder()
                    .success(true)
                    .message("Lấy danh sách liên hệ thành công")
                    .data(contacts)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error fetching contacts: ", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<ContactResponseDto>>builder()
                            .success(false)
                            .message("Lỗi khi lấy danh sách liên hệ")
                            .build());
        }
    }

    /**
     * Get contacts with pagination (Admin only)
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get contacts with pagination", 
               description = "Retrieve PC build consultation requests with pagination (Admin only)",
               security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<ApiResponse<Page<ContactResponseDto>>> getAllContactsWithPagination(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Admin fetching contacts with pagination: page {}, size {}", page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ContactResponseDto> contactPage = contactService.getAllContacts(pageable);
            
            return ResponseEntity.ok(ApiResponse.<Page<ContactResponseDto>>builder()
                    .success(true)
                    .message("Lấy danh sách liên hệ thành công")
                    .data(contactPage)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error fetching contacts with pagination: ", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<Page<ContactResponseDto>>builder()
                            .success(false)
                            .message("Lỗi khi lấy danh sách liên hệ")
                            .build());
        }
    }

    /**
     * Get contact by ID (Admin only)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get contact by ID", 
               description = "Retrieve specific consultation request by ID (Admin only)",
               security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<ApiResponse<ContactResponseDto>> getContactById(@PathVariable UUID id) {
        
        log.info("Admin fetching contact by ID: {}", id);
        
        try {
            ContactResponseDto contact = contactService.getContactById(id);
            
            return ResponseEntity.ok(ApiResponse.<ContactResponseDto>builder()
                    .success(true)
                    .message("Lấy thông tin liên hệ thành công")
                    .data(contact)
                    .build());
                    
        } catch (RuntimeException e) {
            log.warn("Contact not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<ContactResponseDto>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
                            
        } catch (Exception e) {
            log.error("Error fetching contact by ID {}: ", id, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<ContactResponseDto>builder()
                            .success(false)
                            .message("Lỗi khi lấy thông tin liên hệ")
                            .build());
        }
    }

    /**
     * Delete contact by ID (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete contact", 
               description = "Delete consultation request by ID (Admin only)",
               security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<ApiResponse<String>> deleteContact(@PathVariable UUID id) {
        
        log.info("Admin deleting contact: {}", id);
        
        try {
            contactService.deleteContact(id);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Xóa liên hệ thành công")
                    .data("Contact deleted: " + id)
                    .build());
                    
        } catch (RuntimeException e) {
            log.warn("Contact not found for deletion: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
                            
        } catch (Exception e) {
            log.error("Error deleting contact {}: ", id, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message("Lỗi khi xóa liên hệ")
                            .build());
        }
    }

    /**
     * Search contacts by phone (Admin only)
     */
    @GetMapping("/search/phone")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search contacts by phone", 
               description = "Search consultation requests by phone number (Admin only)",
               security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<ApiResponse<List<ContactResponseDto>>> searchContactsByPhone(
            @RequestParam String phone) {
        
        log.info("Admin searching contacts by phone: {}", phone);
        
        try {
            List<ContactResponseDto> contacts = contactService.findContactsByPhone(phone);
            
            return ResponseEntity.ok(ApiResponse.<List<ContactResponseDto>>builder()
                    .success(true)
                    .message("Tìm kiếm liên hệ thành công")
                    .data(contacts)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error searching contacts by phone: ", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<ContactResponseDto>>builder()
                            .success(false)
                            .message("Lỗi khi tìm kiếm liên hệ")
                            .build());
        }
    }

    /**
     * Get contact statistics (Admin only)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get contact statistics", 
               description = "Get consultation request statistics (Admin only)",
               security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<ApiResponse<ContactService.ContactStatisticsDto>> getContactStatistics() {
        
        log.info("Admin fetching contact statistics");
        
        try {
            ContactService.ContactStatisticsDto statistics = contactService.getContactStatistics();
            
            return ResponseEntity.ok(ApiResponse.<ContactService.ContactStatisticsDto>builder()
                    .success(true)
                    .message("Lấy thống kê liên hệ thành công")
                    .data(statistics)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error fetching contact statistics: ", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<ContactService.ContactStatisticsDto>builder()
                            .success(false)
                            .message("Lỗi khi lấy thống kê liên hệ")
                            .build());
        }
    }
}
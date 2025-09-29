package com.example.ComputerStore.controller;

import com.example.ComputerStore.dto.request.CreateBlogDto;
import com.example.ComputerStore.dto.response.ApiResponse;
import com.example.ComputerStore.dto.response.BlogResponseDto;
import com.example.ComputerStore.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller for Blog management
 * Handles blog CRUD operations and search functionality
 */
@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Blog Management", description = "APIs for managing blog posts and articles")
public class BlogController {

    private final BlogService blogService;

    @Operation(
            summary = "Create new blog post",
            description = "Create a new blog post. Requires admin privileges."
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponseDto>> createBlog(
            @Valid @RequestBody CreateBlogDto createBlogDto) {
        
        log.info("Admin creating new blog: {}", createBlogDto.getTitle());
        
        BlogResponseDto blogResponse = blogService.createBlog(createBlogDto);
        
        return ResponseEntity.status(201).body(
                ApiResponse.<BlogResponseDto>builder()
                        .success(true)
                        .message("Tạo bài viết thành công")
                        .data(blogResponse)
                        .build()
        );
    }

    @Operation(
            summary = "Get all blogs",
            description = "Retrieve all blog posts ordered by creation date (newest first)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogResponseDto>>> getAllBlogs() {
        
        log.info("Fetching all blogs");
        
        List<BlogResponseDto> blogs = blogService.getAllBlogs();
        
        return ResponseEntity.ok(
                ApiResponse.<List<BlogResponseDto>>builder()
                        .success(true)
                        .message("Lấy danh sách bài viết thành công")
                        .data(blogs)
                        .build()
        );
    }

    @Operation(
            summary = "Get blogs with pagination",
            description = "Retrieve blogs with pagination support"
    )
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<BlogResponseDto>>> getAllBlogsWithPagination(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        log.info("Fetching blogs with pagination: page {}, size {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogResponseDto> blogPage = blogService.getAllBlogs(pageable);
        
        return ResponseEntity.ok(
                ApiResponse.<Page<BlogResponseDto>>builder()
                        .success(true)
                        .message("Lấy danh sách bài viết thành công")
                        .data(blogPage)
                        .build()
        );
    }

    @Operation(
            summary = "Get blog by ID",
            description = "Retrieve a specific blog post by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogResponseDto>> getBlogById(
            @Parameter(description = "Blog ID") @PathVariable UUID id) {
        
        log.info("Fetching blog by ID: {}", id);
        
        BlogResponseDto blog = blogService.getBlogById(id);
        
        return ResponseEntity.ok(
                ApiResponse.<BlogResponseDto>builder()
                        .success(true)
                        .message("Lấy bài viết thành công")
                        .data(blog)
                        .build()
        );
    }

    @Operation(
            summary = "Update blog post",
            description = "Update an existing blog post. Requires admin privileges."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BlogResponseDto>> updateBlog(
            @Parameter(description = "Blog ID") @PathVariable UUID id,
            @Valid @RequestBody CreateBlogDto updateBlogDto) {
        
        log.info("Admin updating blog: {}", id);
        
        BlogResponseDto updatedBlog = blogService.updateBlog(id, updateBlogDto);
        
        return ResponseEntity.ok(
                ApiResponse.<BlogResponseDto>builder()
                        .success(true)
                        .message("Cập nhật bài viết thành công")
                        .data(updatedBlog)
                        .build()
        );
    }

    @Operation(
            summary = "Delete blog post",
            description = "Delete a blog post. Requires admin privileges."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(
            @Parameter(description = "Blog ID") @PathVariable UUID id) {
        
        log.info("Admin deleting blog: {}", id);
        
        blogService.deleteBlog(id);
        
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Xóa bài viết thành công")
                        .build()
        );
    }

    @Operation(
            summary = "Search blogs by title",
            description = "Search for blog posts by title keyword"
    )
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<List<BlogResponseDto>>> searchBlogsByTitle(
            @Parameter(description = "Title keyword") @RequestParam String title) {
        
        log.info("Searching blogs by title: {}", title);
        
        List<BlogResponseDto> blogs = blogService.searchBlogsByTitle(title);
        
        return ResponseEntity.ok(
                ApiResponse.<List<BlogResponseDto>>builder()
                        .success(true)
                        .message("Tìm kiếm bài viết thành công")
                        .data(blogs)
                        .build()
        );
    }

    @Operation(
            summary = "Search blogs by title with pagination",
            description = "Search for blog posts by title keyword with pagination"
    )
    @GetMapping("/search/title/paginated")
    public ResponseEntity<ApiResponse<Page<BlogResponseDto>>> searchBlogsByTitleWithPagination(
            @Parameter(description = "Title keyword") @RequestParam String title,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        log.info("Searching blogs by title with pagination: {}", title);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BlogResponseDto> blogPage = blogService.searchBlogsByTitle(title, pageable);
        
        return ResponseEntity.ok(
                ApiResponse.<Page<BlogResponseDto>>builder()
                        .success(true)
                        .message("Tìm kiếm bài viết thành công")
                        .data(blogPage)
                        .build()
        );
    }

    @Operation(
            summary = "Search blogs by content",
            description = "Search for blog posts by content keyword"
    )
    @GetMapping("/search/content")
    public ResponseEntity<ApiResponse<List<BlogResponseDto>>> searchBlogsByContent(
            @Parameter(description = "Content keyword") @RequestParam String content) {
        
        log.info("Searching blogs by content: {}", content);
        
        List<BlogResponseDto> blogs = blogService.searchBlogsByContent(content);
        
        return ResponseEntity.ok(
                ApiResponse.<List<BlogResponseDto>>builder()
                        .success(true)
                        .message("Tìm kiếm bài viết thành công")
                        .data(blogs)
                        .build()
        );
    }

    @Operation(
            summary = "Get recent blogs",
            description = "Get recent blog posts (limited number)"
    )
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<BlogResponseDto>>> getRecentBlogs(
            @Parameter(description = "Number of blogs to retrieve") @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Fetching {} recent blogs", limit);
        
        List<BlogResponseDto> blogs = blogService.getRecentBlogs(limit);
        
        return ResponseEntity.ok(
                ApiResponse.<List<BlogResponseDto>>builder()
                        .success(true)
                        .message("Lấy bài viết gần đây thành công")
                        .data(blogs)
                        .build()
        );
    }

    @Operation(
            summary = "Get blogs by date range",
            description = "Get blog posts created within a specific date range. Requires admin privileges."
    )
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BlogResponseDto>>> getBlogsByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd HH:mm:ss)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "End date (yyyy-MM-dd HH:mm:ss)") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        
        log.info("Admin fetching blogs between {} and {}", startDate, endDate);
        
        List<BlogResponseDto> blogs = blogService.getBlogsByDateRange(startDate, endDate);
        
        return ResponseEntity.ok(
                ApiResponse.<List<BlogResponseDto>>builder()
                        .success(true)
                        .message("Lấy bài viết theo khoảng thời gian thành công")
                        .data(blogs)
                        .build()
        );
    }

    @Operation(
            summary = "Get blog statistics",
            description = "Get blog statistics including total count and today's count. Requires admin privileges."
    )
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BlogService.BlogStatisticsDto>> getBlogStatistics() {
        
        log.info("Admin fetching blog statistics");
        
        BlogService.BlogStatisticsDto statistics = blogService.getBlogStatistics();
        
        return ResponseEntity.ok(
                ApiResponse.<BlogService.BlogStatisticsDto>builder()
                        .success(true)
                        .message("Lấy thống kê bài viết thành công")
                        .data(statistics)
                        .build()
        );
    }
}
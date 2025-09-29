package com.example.ComputerStore.service;

import com.example.ComputerStore.dto.request.CreateBlogDto;
import com.example.ComputerStore.dto.response.BlogResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Blog operations
 * Handles blog management business logic
 */
public interface BlogService {

    /**
     * Create a new blog post
     */
    BlogResponseDto createBlog(CreateBlogDto createBlogDto);


    List<BlogResponseDto> getAllBlogs();

    /**
     * Get all blogs with pagination
     */
    Page<BlogResponseDto> getAllBlogs(Pageable pageable);

    BlogResponseDto getBlogById(UUID id);

    /**
     * Update existing blog
     */
    BlogResponseDto updateBlog(UUID id, CreateBlogDto updateBlogDto);

    void deleteBlog(UUID id);

    List<BlogResponseDto> searchBlogsByTitle(String title);

    Page<BlogResponseDto> searchBlogsByTitle(String title, Pageable pageable);

    List<BlogResponseDto> searchBlogsByContent(String content);

    /**
     * Get blogs by date range
     */
    List<BlogResponseDto> getBlogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get recent blogs (limited number)
     */
    List<BlogResponseDto> getRecentBlogs(int limit);

    /**
     * Get blog statistics
     */
    BlogStatisticsDto getBlogStatistics();

    /**
     * Blog statistics inner class
     */
    record BlogStatisticsDto(Long totalBlogs, Long todayBlogs) {}
}
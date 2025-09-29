package com.example.ComputerStore.service.impl;

import com.example.ComputerStore.dto.request.CreateBlogDto;
import com.example.ComputerStore.dto.response.BlogResponseDto;
import com.example.ComputerStore.entity.Blog;
import com.example.ComputerStore.repository.BlogRepository;
import com.example.ComputerStore.service.BlogService;
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
 * Implementation of BlogService
 * Handles blog management operations and business logic
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Override
    public BlogResponseDto createBlog(CreateBlogDto createBlogDto) {
        log.info("Creating new blog: {}", createBlogDto.getTitle());
        
        try {
            // Build Blog entity from DTO
            Blog blog = Blog.builder()
                    .title(createBlogDto.getTitle())
                    .content(createBlogDto.getContent())
                    .image(createBlogDto.getImage())
                    .build();

            // Save to database
            Blog savedBlog = blogRepository.save(blog);
            
            log.info("Blog created successfully with ID: {}", savedBlog.getId());
            
            // Convert to response DTO
            return convertToResponseDto(savedBlog);
            
        } catch (Exception e) {
            log.error("Error creating blog {}: ", createBlogDto.getTitle(), e);
            throw new RuntimeException("Lỗi khi tạo bài viết: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponseDto> getAllBlogs() {
        log.info("Fetching all blogs");
        
        List<Blog> blogs = blogRepository.findAllOrderByCreatedAtDesc();
        
        return blogs.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogResponseDto> getAllBlogs(Pageable pageable) {
        log.info("Fetching blogs with pagination: page {}, size {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Blog> blogPage = blogRepository.findAllOrderByCreatedAtDesc(pageable);
        
        return blogPage.map(this::convertToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponseDto getBlogById(UUID id) {
        log.info("Fetching blog by ID: {}", id);
        
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));
        
        return convertToResponseDto(blog);
    }

    @Override
    public BlogResponseDto updateBlog(UUID id, CreateBlogDto updateBlogDto) {
        log.info("Updating blog with ID: {}", id);
        
        try {
            Blog existingBlog = blogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));

            // Update fields
            existingBlog.setTitle(updateBlogDto.getTitle());
            existingBlog.setContent(updateBlogDto.getContent());
            existingBlog.setImage(updateBlogDto.getImage());

            // Save updated blog
            Blog updatedBlog = blogRepository.save(existingBlog);
            
            log.info("Blog updated successfully: {}", id);
            
            return convertToResponseDto(updatedBlog);
            
        } catch (Exception e) {
            log.error("Error updating blog {}: ", id, e);
            throw new RuntimeException("Lỗi khi cập nhật bài viết: " + e.getMessage());
        }
    }

    @Override
    public void deleteBlog(UUID id) {
        log.info("Deleting blog with ID: {}", id);
        
        if (!blogRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy bài viết với ID: " + id);
        }
        
        blogRepository.deleteById(id);
        log.info("Blog deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponseDto> searchBlogsByTitle(String title) {
        log.info("Searching blogs by title: {}", title);
        
        List<Blog> blogs = blogRepository.findByTitleContainingIgnoreCase(title);
        
        return blogs.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogResponseDto> searchBlogsByTitle(String title, Pageable pageable) {
        log.info("Searching blogs by title with pagination: {}", title);
        
        Page<Blog> blogPage = blogRepository.findByTitleContainingIgnoreCase(title, pageable);
        
        return blogPage.map(this::convertToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponseDto> searchBlogsByContent(String content) {
        log.info("Searching blogs by content: {}", content);
        
        List<Blog> blogs = blogRepository.findByContentContainingIgnoreCase(content);
        
        return blogs.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponseDto> getBlogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching blogs between {} and {}", startDate, endDate);
        
        List<Blog> blogs = blogRepository.findByCreatedAtBetween(startDate, endDate);
        
        return blogs.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogResponseDto> getRecentBlogs(int limit) {
        log.info("Fetching {} recent blogs", limit);
        
        List<Blog> blogs = blogRepository.findRecentBlogs(limit);
        
        return blogs.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BlogStatisticsDto getBlogStatistics() {
        log.info("Calculating blog statistics");
        
        Long totalBlogs = blogRepository.countTotalBlogs();
        Long todayBlogs = blogRepository.countTodayBlogs();
        
        return new BlogStatisticsDto(totalBlogs, todayBlogs);
    }

    /**
     * Convert Blog entity to BlogResponseDto
     */
    private BlogResponseDto convertToResponseDto(Blog blog) {
        return BlogResponseDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .image(blog.getImage())
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }
}
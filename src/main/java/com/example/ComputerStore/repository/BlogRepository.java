package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BlogRepository extends JpaRepository<Blog, UUID> {

    /**
     * Find all blogs ordered by creation date (newest first)
     */
    @Query("SELECT b FROM Blog b ORDER BY b.createdAt DESC")
    List<Blog> findAllOrderByCreatedAtDesc();

    /**
     * Find all blogs with pagination, ordered by creation date (newest first)
     */
    @Query("SELECT b FROM Blog b ORDER BY b.createdAt DESC")
    Page<Blog> findAllOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Search blogs by title (case insensitive)
     */
    @Query("SELECT b FROM Blog b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY b.createdAt DESC")
    List<Blog> findByTitleContainingIgnoreCase(@Param("title") String title);

    /**
     * Search blogs by title with pagination
     */
    @Query("SELECT b FROM Blog b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY b.createdAt DESC")
    Page<Blog> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    /**
     * Search blogs by content (case insensitive)
     */
    @Query("SELECT b FROM Blog b WHERE LOWER(b.content) LIKE LOWER(CONCAT('%', :content, '%')) ORDER BY b.createdAt DESC")
    List<Blog> findByContentContainingIgnoreCase(@Param("content") String content);

    /**
     * Find blogs created between dates
     */
    @Query("SELECT b FROM Blog b WHERE b.createdAt BETWEEN :startDate AND :endDate ORDER BY b.createdAt DESC")
    List<Blog> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(b) FROM Blog b")
    Long countTotalBlogs();

    @Query("SELECT COUNT(b) FROM Blog b WHERE DATE(b.createdAt) = CURRENT_DATE")
    Long countTodayBlogs();

    /**
     * Find recent blogs (limit by number)
     */
    @Query("SELECT b FROM Blog b ORDER BY b.createdAt DESC LIMIT :limit")
    List<Blog> findRecentBlogs(@Param("limit") int limit);
}

package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.Blogs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlogRepository extends JpaRepository<Blogs, UUID> {
    // tim Blog theo title
    @Query("SELECT b FROM Blogs b " +
            "WHERE b.title LIKE %:title%")
    List<Blogs> findByTitleContaining(@Param("title") String title);

    @Query("SELECT b FROM Blogs b " +
            "WHERE b.title LIKE %:title% ")
    Page<Blogs> findByTitleContaining(@Param("title") String title, Pageable pageable);

    // Tim blogs theo title hoac content
    @Query("SELECT b FROM Blogs b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    List<Blogs> findByTitleOrContentContaining(@Param("keyword") String keyword);

    @Query("SELECT b FROM Blogs b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<Blogs> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    // Latest blogs
    List<Blogs> findTop10ByOrderByCreatedAtDesc();

    // All blogs ordered by created date
    Page<Blogs> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Count total blogs
    @Query("SELECT COUNT(b) FROM Blogs b")
    long countTotalBlogs();

}

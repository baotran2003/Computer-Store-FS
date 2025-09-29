package com.example.ComputerStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Blog Entity for Computer Store Articles and News
 * Stores blog posts, tutorials, and tbao
 */
@Entity
@Table(name = "blogs")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Blog featured image URL
     */
    @Column(name = "image", nullable = false, length = 1000)
    private String image;
}
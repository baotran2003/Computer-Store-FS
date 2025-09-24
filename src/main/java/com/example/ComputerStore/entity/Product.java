package com.example.ComputerStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import com.example.ComputerStore.enumeric.ComponentType;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false, precision = 5, scale = 2, columnDefinition = "DECIMAL(5,2) DEFAULT 0")
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String images;

    @Column(nullable = false)
    private Integer stock;

    @Column(columnDefinition = "TEXT")
    private String cpu;

    @Column(columnDefinition = "TEXT")
    private String main;

    @Column(columnDefinition = "TEXT")
    private String ram;

    @Column(columnDefinition = "TEXT")
    private String storage;

    @Column(columnDefinition = "TEXT")
    private String gpu;

    @Column(columnDefinition = "TEXT")
    private String power;

    @Column(name = "case_computer", columnDefinition = "TEXT")
    private String caseComputer;

    @Column(columnDefinition = "TEXT")
    private String coolers;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", nullable = false)
    private ComponentType componentType;

    // Relationship
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<BuildPcCart> buildPcCarts = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<UserWatchProduct> userWatchProducts = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductPreview> productPreviews = new ArrayList<>();

}

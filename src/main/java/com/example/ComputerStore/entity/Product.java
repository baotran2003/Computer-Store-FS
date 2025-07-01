package com.example.ComputerStore.entity;

import com.example.ComputerStore.enumeric.ComponentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(nullable = false)
    private Float price;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer discount;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String images;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

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


    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}

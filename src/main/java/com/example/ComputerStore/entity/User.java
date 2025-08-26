package com.example.ComputerStore.entity;

import com.example.ComputerStore.enumeric.TypeLogin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column
    private String phone;

    @Column
    private String address;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(name = "is_admin", nullable = false, columnDefinition = "VARCHAR(1) DEFAULT '0'")
    private String isAdmin = "0";

    @Enumerated(EnumType.STRING)
    @Column(name = "type_login", nullable = false)
    private TypeLogin typeLogin;

    // Relationship
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private ApiKey apiKey;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BuildPcCart> buildPcCarts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserWatchProduct> userWatchProducts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProductPreview> productPreviews = new ArrayList<>();

}

package com.example.ComputerStore.repository;

import com.example.ComputerStore.entity.User;
import com.example.ComputerStore.enumeric.TypeLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Tim user theo email
    Optional<User> findByEmail(String email);

    // check email ton tai
    boolean existsByEmail(String email);

    // tim user theo phone
    List<User> findByPhone(String phone);

    // tim user theo type login
    List<User> findByTypeLogin(TypeLogin typeLogin);

    // Tim admin users
    List<User> findByIsAdmin(String isAdmin);

    // Tìm user theo email và password (cho login)
    Optional<User> findByEmailAndPassword(String email, String password);

    // Tim user theo fullName (search)
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:fullName%")
    List<User> findByFullNameContaining(@Param("fullName") String fullName);

    // Tim user theo email hoac phone
    @Query("SELECT u FROM User u WHERE u.email = :emailOrPhone OR u.phone = :emailOrPhone")
    Optional<User> findByEmailOrPhone(@Param("emailOrPhone") String emailOrPhone);

    // Count total users
    @Query("SELECT COUNT(u) FROM User u")
    long countTotalUsers();

    // Count users by type login
    @Query("SELECT COUNT(u) FROM User u WHERE u.typeLogin = :typeLogin")
    long countByTypeLogin(@Param("typeLogin") TypeLogin typeLogin);
}

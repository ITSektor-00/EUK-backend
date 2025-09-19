package com.sirus.backend.repository;

import com.sirus.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // Dodatne metode za UserService
    List<User> findByRole(String role);
    
    Page<User> findByRole(String role, Pageable pageable);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    
    Page<User> findByRoleAndIsActive(String role, Boolean isActive, Pageable pageable);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByIsActiveFalse();
    
    long countByIsActiveTrue();
    
    long countByIsActiveFalse();
    
    // Pretraga po više polja
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> findByUsernameContainingOrEmailContainingOrFirstNameContainingOrLastNameContaining(
        @Param("search") String search, 
        @Param("search") String search2, 
        @Param("search") String search3, 
        @Param("search") String search4, 
        Pageable pageable);
} 
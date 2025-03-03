package com.whimsy.wish.repository;

import com.whimsy.wish.domain.user.Role;
import com.whimsy.wish.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Find users by role
    List<User> findByRole(Role role);
    Page<User> findByRole(Role role, Pageable pageable);
    
    // Find users by name
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    Page<User> findByName(@Param("name") String name, Pageable pageable);
    
    // Find recently registered users
    List<User> findByCreatedAtGreaterThan(LocalDateTime date);
    
    // Count by role
    long countByRole(Role role);
    
    // Find users with specific phone number pattern
    @Query("SELECT u FROM User u WHERE u.phoneNumber LIKE :phonePattern")
    List<User> findByPhoneNumberPattern(@Param("phonePattern") String phonePattern);
} 
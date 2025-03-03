package com.whimsy.wish.repository;

import com.whimsy.wish.domain.user.Address;
import com.whimsy.wish.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUser(User user);
    List<Address> findByUserAndIsDefaultTrue(User user);
    Optional<Address> findByIdAndUser(UUID id, User user);
    void deleteByIdAndUser(UUID id, User user);
} 
package com.whimsy.wish.service;

import com.whimsy.wish.dto.user.UserCreateRequest;
import com.whimsy.wish.dto.user.UserResponse;
import com.whimsy.wish.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUserById(UUID id);
    UserResponse getUserByEmail(String email);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUser(UUID id, UserUpdateRequest request);
    void deleteUser(UUID id);
    boolean existsByEmail(String email);
} 
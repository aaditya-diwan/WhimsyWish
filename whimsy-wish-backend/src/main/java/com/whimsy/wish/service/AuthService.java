package com.whimsy.wish.service;

import com.whimsy.wish.dto.auth.AuthRequest;
import com.whimsy.wish.dto.auth.AuthResponse;
import com.whimsy.wish.dto.auth.RefreshTokenRequest;
import com.whimsy.wish.dto.user.UserCreateRequest;

public interface AuthService {
    AuthResponse register(UserCreateRequest request);
    AuthResponse authenticate(AuthRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
} 
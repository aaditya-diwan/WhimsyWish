package com.whimsy.wish.service.impl;

import com.whimsy.wish.domain.user.User;
import com.whimsy.wish.dto.auth.AuthRequest;
import com.whimsy.wish.dto.auth.AuthResponse;
import com.whimsy.wish.dto.auth.RefreshTokenRequest;
import com.whimsy.wish.dto.user.UserCreateRequest;
import com.whimsy.wish.dto.user.UserResponse;
import com.whimsy.wish.exception.ResourceNotFoundException;
import com.whimsy.wish.repository.UserRepository;
import com.whimsy.wish.security.JwtTokenProvider;
import com.whimsy.wish.service.AuthService;
import com.whimsy.wish.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(UserCreateRequest request) {
        log.debug("Registering new user with email: {}", request.getEmail());

        UserResponse userResponse = userService.createUser(request);

        // Authenticate the newly created user
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate tokens
            String accessToken = tokenProvider.createToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication);

            log.debug("User registered successfully: {}", userResponse.getEmail());

            return AuthResponse.builder()
                    .userId(userResponse.getId())
                    .email(userResponse.getEmail())
                    .firstName(userResponse.getFirstName())
                    .lastName(userResponse.getLastName())
                    .role(userResponse.getRole())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Error during authentication after registration: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        log.debug("Authenticating user: {}", request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        // Get user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        log.debug("User authenticated successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.debug("Refreshing token");

        // Validate refresh token
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // Get username from token
        String username = tokenProvider.getUsername(request.getRefreshToken());
        log.debug("Username from refresh token: {}", username);

        // Get user details
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));

        // Create authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username, null, user.getAuthorities());

        // Generate new tokens
        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
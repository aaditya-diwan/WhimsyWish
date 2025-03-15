package com.whimsy.wish.security;

import com.whimsy.wish.domain.user.User;
import com.whimsy.wish.exception.UnauthorizedException;
import com.whimsy.wish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    /**
     * Get the currently authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found but was authenticated"));
    }

    /**
     * Validate that the current user has admin access
     */
    public void validateAdminAccess() {
        User currentUser = getCurrentUser();
        if (!currentUser.isAdmin()) {
            throw new UnauthorizedException("Admin access required");
        }
    }

    /**
     * Validate that the current user has admin access or is accessing their own data
     */
    public void validateAdminOrSelfAccess(UUID userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.isAdmin() && !currentUser.getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to access this resource");
        }
    }
}
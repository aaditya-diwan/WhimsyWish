package com.whimsy.wish.controller;

import com.whimsy.wish.dto.address.AddressCreateRequest;
import com.whimsy.wish.dto.address.AddressResponse;
import com.whimsy.wish.dto.address.AddressUpdateRequest;
import com.whimsy.wish.dto.common.ApiResponse;
import com.whimsy.wish.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<AddressResponse> createAddress(
            @PathVariable UUID userId,
            @Valid @RequestBody AddressCreateRequest request) {
        return ResponseEntity.ok(addressService.createAddress(userId, request));
    }

    @GetMapping("/{addressId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<AddressResponse> getAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId) {
        return ResponseEntity.ok(addressService.getAddressById(userId, addressId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(@PathVariable UUID userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @PutMapping("/{addressId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressUpdateRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(userId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse> deleteAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }

    @PatchMapping("/{addressId}/default")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId) {
        return ResponseEntity.ok(addressService.setDefaultAddress(userId, addressId));
    }
} 
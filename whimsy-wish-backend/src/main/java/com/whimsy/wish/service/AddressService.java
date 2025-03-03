package com.whimsy.wish.service;

import com.whimsy.wish.dto.address.AddressCreateRequest;
import com.whimsy.wish.dto.address.AddressResponse;
import com.whimsy.wish.dto.address.AddressUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse createAddress(UUID userId, AddressCreateRequest request);
    AddressResponse getAddressById(UUID userId, UUID addressId);
    List<AddressResponse> getUserAddresses(UUID userId);
    AddressResponse updateAddress(UUID userId, UUID addressId, AddressUpdateRequest request);
    void deleteAddress(UUID userId, UUID addressId);
    AddressResponse setDefaultAddress(UUID userId, UUID addressId);
} 
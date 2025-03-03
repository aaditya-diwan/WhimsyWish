package com.whimsy.wish.service.impl;

import com.whimsy.wish.domain.user.Address;
import com.whimsy.wish.domain.user.User;
import com.whimsy.wish.dto.address.AddressCreateRequest;
import com.whimsy.wish.dto.address.AddressResponse;
import com.whimsy.wish.dto.address.AddressUpdateRequest;
import com.whimsy.wish.exception.ResourceNotFoundException;
import com.whimsy.wish.repository.AddressRepository;
import com.whimsy.wish.repository.UserRepository;
import com.whimsy.wish.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AddressResponse createAddress(UUID userId, AddressCreateRequest request) {
        User user = getUserById(userId);
        
        // If this is the first address or set as default, ensure it's the only default
        if (request.isDefault()) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .forEach(address -> {
                        address.setDefault(false);
                        addressRepository.save(address);
                    });
        }
        
        Address address = Address.builder()
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .additionalInfo(request.getAdditionalInfo())
                .isDefault(request.isDefault())
                .user(user)
                .build();
        
        return mapToAddressResponse(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(UUID userId, UUID addressId) {
        User user = getUserById(userId);
        Address address = getAddressByIdAndUser(addressId, user);
        
        return mapToAddressResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(UUID userId) {
        User user = getUserById(userId);
        return addressRepository.findByUser(user).stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(UUID userId, UUID addressId, AddressUpdateRequest request) {
        User user = getUserById(userId);
        Address address = getAddressByIdAndUser(addressId, user);
        
        if (request.getStreetAddress() != null) {
            address.setStreetAddress(request.getStreetAddress());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getState() != null) {
            address.setState(request.getState());
        }
        if (request.getPostalCode() != null) {
            address.setPostalCode(request.getPostalCode());
        }
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }
        if (request.getAdditionalInfo() != null) {
            address.setAdditionalInfo(request.getAdditionalInfo());
        }
        if (request.getIsDefault() != null && request.getIsDefault()) {
            // Unset any other default address
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .forEach(defaultAddress -> {
                        if (!defaultAddress.getId().equals(addressId)) {
                            defaultAddress.setDefault(false);
                            addressRepository.save(defaultAddress);
                        }
                    });
            address.setDefault(true);
        }
        
        return mapToAddressResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        User user = getUserById(userId);
        // Verify the address exists for this user
        getAddressByIdAndUser(addressId, user);
        addressRepository.deleteByIdAndUser(addressId, user);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(UUID userId, UUID addressId) {
        User user = getUserById(userId);
        Address address = getAddressByIdAndUser(addressId, user);
        
        // Unset any current default addresses
        addressRepository.findByUserAndIsDefaultTrue(user)
                .forEach(defaultAddress -> {
                    defaultAddress.setDefault(false);
                    addressRepository.save(defaultAddress);
                });
        
        // Set the new default
        address.setDefault(true);
        return mapToAddressResponse(addressRepository.save(address));
    }
    
    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
    
    private Address getAddressByIdAndUser(UUID addressId, User user) {
        return addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId + " for user: " + user.getId()));
    }
    
    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .streetAddress(address.getStreetAddress())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .additionalInfo(address.getAdditionalInfo())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
} 
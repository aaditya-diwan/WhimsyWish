package com.whimsy.wish.dto.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateRequest {
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String additionalInfo;
    private Boolean isDefault;
} 
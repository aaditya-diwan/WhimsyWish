package com.whimsy.wish.dto.order;

import com.whimsy.wish.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {
    private OrderStatus status;
    private UUID shippingAddressId;
    private UUID billingAddressId;
    private String notes;
    private String cancelReason;
    private String statusChangeComment;
} 
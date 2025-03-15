package com.whimsy.wish.dto.order;

import com.whimsy.wish.domain.order.OrderStatus;
import com.whimsy.wish.dto.address.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String orderNumber;
    private UUID userId;
    private String userEmail;
    private AddressResponse shippingAddress;
    private AddressResponse billingAddress;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    private BigDecimal total;
    private OrderStatus status;
    private String paymentIntentId;
    private LocalDateTime paymentDate;
    private LocalDateTime shippingDate;
    private LocalDateTime deliveryDate;
    private String notes;
    private String cancelReason;
    private List<OrderItemResponse> items;
    private List<OrderStatusHistoryResponse> statusHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
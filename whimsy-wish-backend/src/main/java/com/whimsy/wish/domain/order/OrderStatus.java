package com.whimsy.wish.domain.order;

public enum OrderStatus {
    CREATED,           // Initial state when order is created
    PAYMENT_PENDING,   // Waiting for payment
    PAYMENT_COMPLETE,  // Payment has been received
    PROCESSING,        // Order is being processed
    PACKED,            // Order has been packed
    SHIPPED,           // Order has been shipped
    DELIVERED,         // Order has been delivered
    CANCELLED,         // Order has been cancelled
    REFUNDED,          // Order has been refunded
    RETURNED           // Order has been returned
} 
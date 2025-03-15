package com.whimsy.wish.dto.order;

import com.whimsy.wish.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryResponse {
    private UUID id;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private String comment;
    private LocalDateTime createdAt;
    private String timestamp;
} 
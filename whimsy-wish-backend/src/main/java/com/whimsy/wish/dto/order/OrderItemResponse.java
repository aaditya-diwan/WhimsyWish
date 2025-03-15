package com.whimsy.wish.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String productSku;
    private BigDecimal price;
    private Integer quantity;
    private String customization;
    private String productDetails;
    private BigDecimal subtotal;
} 
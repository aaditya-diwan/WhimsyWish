package com.whimsy.wish.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private UUID categoryId;
    private String categoryName;
    private Set<ProductAttributeDto> attributes;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
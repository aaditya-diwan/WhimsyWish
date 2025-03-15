package com.whimsy.wish.domain.order;

import com.whimsy.wish.domain.common.BaseEntity;
import com.whimsy.wish.domain.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_sku", nullable = false)
    private String productSku;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    // Optional product customization info
    @Column(length = 500)
    private String customization;

    // Store snapshot of product details at time of order
    @Column(name = "product_details", length = 1000)
    private String productDetails;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    // Helper method to calculate the subtotal
    public void calculateSubtotal() {
        this.subtotal = this.price.multiply(new BigDecimal(this.quantity));
    }
} 
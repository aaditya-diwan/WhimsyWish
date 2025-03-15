package com.whimsy.wish.domain.order;

import com.whimsy.wish.domain.common.BaseEntity;
import com.whimsy.wish.domain.user.Address;
import com.whimsy.wish.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id", nullable = false)
    private Address billingAddress;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(nullable = false)
    private BigDecimal tax;

    @Column(name = "shipping_cost", nullable = false)
    private BigDecimal shippingCost;

    @Column(nullable = false)
    private BigDecimal discount;

    @Column(nullable = false)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "payment_intent_id", nullable = true)
    private String paymentIntentId;

    @Column(name = "payment_date", nullable = true)
    private LocalDateTime paymentDate;

    @Column(name = "shipping_date", nullable = true)
    private LocalDateTime shippingDate;

    @Column(name = "delivery_date", nullable = true)
    private LocalDateTime deliveryDate;

    @Column(length = 1000)
    private String notes;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

    // Helper method to calculate order totals
    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.total = this.subtotal
                .add(this.tax)
                .add(this.shippingCost)
                .subtract(this.discount);
    }

    // Helper method to add an item to the order
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    // Helper method to remove an item from the order
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    // Helper method to add status history
    public void addStatusHistory(OrderStatus newStatus, String comment) {
        OrderStatusHistory statusChange = OrderStatusHistory.builder()
                .order(this)
                .previousStatus(this.status)
                .newStatus(newStatus)
                .comment(comment)
                .build();
        
        this.statusHistory.add(statusChange);
        this.status = newStatus;
    }
} 
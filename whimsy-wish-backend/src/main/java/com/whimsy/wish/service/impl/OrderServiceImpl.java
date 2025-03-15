package com.whimsy.wish.service.impl;

import com.whimsy.wish.domain.order.Order;
import com.whimsy.wish.domain.order.OrderItem;
import com.whimsy.wish.domain.order.OrderStatus;
import com.whimsy.wish.domain.product.Product;
import com.whimsy.wish.domain.user.Address;
import com.whimsy.wish.domain.user.User;
import com.whimsy.wish.dto.address.AddressResponse;
import com.whimsy.wish.dto.order.*;
import com.whimsy.wish.exception.ResourceNotFoundException;
import com.whimsy.wish.repository.OrderItemRepository;
import com.whimsy.wish.repository.OrderRepository;
import com.whimsy.wish.repository.OrderStatusHistoryRepository;
import com.whimsy.wish.repository.product.ProductRepository;
import com.whimsy.wish.repository.AddressRepository;
import com.whimsy.wish.repository.UserRepository;
import com.whimsy.wish.security.SecurityUtils;
import com.whimsy.wish.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        
        // Fetch addresses
        Address shippingAddress = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));
        
        Address billingAddress = addressRepository.findById(request.getBillingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Billing address not found"));
        
        // Validate addresses belong to the current user
        if (!shippingAddress.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Shipping address does not belong to the current user");
        }
        
        if (!billingAddress.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Billing address does not belong to the current user");
        }
        
        // Create order with initial fields
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(currentUser)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .subtotal(BigDecimal.ZERO)
                .tax(calculateTax())
                .shippingCost(calculateShippingCost())
                .discount(BigDecimal.ZERO) // Apply discounts if any
                .total(BigDecimal.ZERO) // Will be calculated after adding items
                .status(OrderStatus.CREATED)
                .notes(request.getNotes())
                .build();
        
        // Add items to the order
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.getProductId()));
            
            // Check if product is in stock
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }
            
            // Reduce product stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
            
            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .price(product.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .build();
            
            order.addItem(orderItem);
        }
        
        // Calculate order totals
        order.calculateTotals();
        
        // Add initial status history
        order.addStatusHistory(OrderStatus.CREATED, "Order created");
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Convert to response
        return mapOrderToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID id) {
        Order order = findOrderById(id);
        return mapOrderToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return mapOrderToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        // Check if user has admin role
        securityUtils.validateAdminAccess();
        
        return orderRepository.findAll(pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getCurrentUserOrders(Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        return orderRepository.findByUser(currentUser, pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(UUID userId, Pageable pageable) {
        // Check if user has admin role or is requesting their own orders
        securityUtils.validateAdminOrSelfAccess(userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return orderRepository.findByUser(user, pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        // Check if user has admin role
        securityUtils.validateAdminAccess();
        
        return orderRepository.findByStatus(status).stream()
                .map(this::mapOrderToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(UUID id, OrderUpdateRequest request) {
        Order order = findOrderById(id);
        
        // Validate order is in a state that can be updated
        if (order.getStatus() != OrderStatus.CREATED && 
            order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Order cannot be updated in its current state: " + order.getStatus());
        }
        
        // Update notes if provided
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
        
        // Save the updated order
        Order updatedOrder = orderRepository.save(order);
        
        return mapOrderToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID id, OrderStatusUpdateRequest request) {
        Order order = findOrderById(id);
        
        // Validate the status transition
        validateStatusTransition(order.getStatus(), request.getStatus());
        
        // Update the status and add to history
        order.addStatusHistory(request.getStatus(), request.getComment());
        
        // If order is being marked as cancelled, we need to restore stock
        if (request.getStatus() == OrderStatus.CANCELLED) {
            restoreProductStock(order);
            order.setCancelReason(request.getComment());
        }
        
        // Update timestamps based on status
        updateOrderTimestamps(order, request.getStatus());
        
        // Save the updated order
        Order updatedOrder = orderRepository.save(order);
        
        return mapOrderToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID id, String reason) {
        Order order = findOrderById(id);
        
        // Validate order can be cancelled
        if (order.getStatus() == OrderStatus.SHIPPED || 
            order.getStatus() == OrderStatus.DELIVERED || 
            order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order cannot be cancelled in its current state: " + order.getStatus());
        }
        
        // Set cancel reason
        order.setCancelReason(reason);
        
        // Update the status and add to history
        order.addStatusHistory(OrderStatus.CANCELLED, reason);
        
        // Restore product stock
        restoreProductStock(order);
        
        // Save the updated order
        Order updatedOrder = orderRepository.save(order);
        
        return mapOrderToResponse(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(String keyword, Pageable pageable) {
        // Check if user has admin role
        securityUtils.validateAdminAccess();
        
        return orderRepository.searchAllOrders(keyword, pageable)
                .map(this::mapOrderToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchUserOrders(UUID userId, String keyword, Pageable pageable) {
        // Check if user has admin role or is requesting their own orders
        securityUtils.validateAdminOrSelfAccess(userId);
        
        return orderRepository.searchUserOrders(userId, keyword, pageable)
                .map(this::mapOrderToResponse);
    }

    // Helper methods
    private Order findOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        // Check if the user is admin or the order belongs to the current user
        User currentUser = securityUtils.getCurrentUser();
        if (!currentUser.isAdmin() && !order.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You don't have permission to access this order");
        }
        
        return order;
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Implement logic to validate valid status transitions
        // This is a simplified validation - in a real application, you might want to use a state machine
        if (currentStatus == newStatus) {
            return; // No change, always valid
        }
        
        switch (currentStatus) {
            case CREATED:
                if (newStatus != OrderStatus.PAYMENT_PENDING && 
                    newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
                
            case PAYMENT_PENDING:
                if (newStatus != OrderStatus.PAYMENT_COMPLETE && 
                    newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
                
            case PAYMENT_COMPLETE:
                if (newStatus != OrderStatus.PROCESSING && 
                    newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
                
            case PROCESSING:
                if (newStatus != OrderStatus.PACKED && 
                    newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
                
            case PACKED:
                if (newStatus != OrderStatus.SHIPPED && 
                    newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
                
            case SHIPPED:
                if (newStatus != OrderStatus.DELIVERED && 
                    newStatus != OrderStatus.RETURNED) {
                    throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
                
            case DELIVERED:
                if (newStatus != OrderStatus.RETURNED) {
                    throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
                
            case CANCELLED:
                if (newStatus != OrderStatus.REFUNDED) {
                    throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
                
            case RETURNED:
                if (newStatus != OrderStatus.REFUNDED) {
                    throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
                
            case REFUNDED:
                throw new IllegalStateException("Cannot transition from REFUNDED state");
        }
    }

    private void updateOrderTimestamps(Order order, OrderStatus newStatus) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (newStatus) {
            case PAYMENT_COMPLETE:
                order.setPaymentDate(now);
                break;
            case SHIPPED:
                order.setShippingDate(now);
                break;
            case DELIVERED:
                order.setDeliveryDate(now);
                break;
        }
    }

    private void restoreProductStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
    }

    private String generateOrderNumber() {
        // Simple implementation - in production, you'd want something more sophisticated
        return "ORD-" + System.currentTimeMillis();
    }

    private BigDecimal calculateTax() {
        // Simplified tax calculation
        return new BigDecimal("0.00");
    }

    private BigDecimal calculateShippingCost() {
        // Simplified shipping calculation
        return new BigDecimal("5.00");
    }

    private OrderResponse mapOrderToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .shippingAddress(mapAddressToResponse(order.getShippingAddress()))
                .billingAddress(mapAddressToResponse(order.getBillingAddress()))
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .shippingCost(order.getShippingCost())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .status(order.getStatus())
                .paymentIntentId(order.getPaymentIntentId())
                .paymentDate(order.getPaymentDate())
                .shippingDate(order.getShippingDate())
                .deliveryDate(order.getDeliveryDate())
                .notes(order.getNotes())
                .cancelReason(order.getCancelReason())
                .items(mapOrderItems(order.getItems()))
                .statusHistory(mapStatusHistory(order.getStatusHistory()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private AddressResponse mapAddressToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getUser().getFirstName() +  " " + address.getUser().getLastName())
                .streetAddress(address.getStreetAddress())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phoneNumber(address.getPhoneNumber())
                .isDefault(address.isDefault())
                .build();
    }

    private List<OrderItemResponse> mapOrderItems(List<OrderItem> items) {
        return items.stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .productSku(item.getProductSku())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    private List<OrderStatusHistoryResponse> mapStatusHistory(List<com.whimsy.wish.domain.order.OrderStatusHistory> history) {
        return history.stream()
                .map(statusChange -> OrderStatusHistoryResponse.builder()
                        .id(statusChange.getId())
                        .previousStatus(statusChange.getPreviousStatus())
                        .newStatus(statusChange.getNewStatus())
                        .comment(statusChange.getComment())
                        .timestamp(statusChange.getCreatedAt().toString())
                        .build())
                .collect(Collectors.toList());
    }
} 
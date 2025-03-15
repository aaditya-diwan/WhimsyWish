package com.whimsy.wish.service;

import com.whimsy.wish.domain.order.OrderStatus;
import com.whimsy.wish.dto.order.OrderCreateRequest;
import com.whimsy.wish.dto.order.OrderResponse;
import com.whimsy.wish.dto.order.OrderStatusUpdateRequest;
import com.whimsy.wish.dto.order.OrderUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    
    /**
     * Create a new order for the current authenticated user
     */
    OrderResponse createOrder(OrderCreateRequest request);
    
    /**
     * Get an order by its ID
     */
    OrderResponse getOrder(UUID id);
    
    /**
     * Get an order by its order number
     */
    OrderResponse getOrderByOrderNumber(String orderNumber);
    
    /**
     * Get all orders with pagination
     */
    Page<OrderResponse> getAllOrders(Pageable pageable);
    
    /**
     * Get orders for the current authenticated user with pagination
     */
    Page<OrderResponse> getCurrentUserOrders(Pageable pageable);
    
    /**
     * Get orders for a specific user with pagination
     */
    Page<OrderResponse> getUserOrders(UUID userId, Pageable pageable);
    
    /**
     * Get orders with a specific status
     */
    List<OrderResponse> getOrdersByStatus(OrderStatus status);
    
    /**
     * Update an existing order
     */
    OrderResponse updateOrder(UUID id, OrderUpdateRequest request);
    
    /**
     * Update the status of an order
     */
    OrderResponse updateOrderStatus(UUID id, OrderStatusUpdateRequest request);
    
    /**
     * Cancel an order
     */
    OrderResponse cancelOrder(UUID id, String reason);
    
    /**
     * Search orders by keyword
     */
    Page<OrderResponse> searchOrders(String keyword, Pageable pageable);
    
    /**
     * Search a user's orders by keyword
     */
    Page<OrderResponse> searchUserOrders(UUID userId, String keyword, Pageable pageable);
} 
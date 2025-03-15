package com.whimsy.wish.repository;

import com.whimsy.wish.domain.order.Order;
import com.whimsy.wish.domain.order.OrderStatus;
import com.whimsy.wish.domain.order.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
    
    List<OrderStatusHistory> findByOrderOrderByCreatedAtDesc(Order order);
    
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.order.id = :orderId ORDER BY osh.createdAt DESC")
    List<OrderStatusHistory> findOrderStatusHistoryByOrderId(@Param("orderId") UUID orderId);
    
    @Query("SELECT osh FROM OrderStatusHistory osh " +
           "WHERE osh.newStatus = :status AND osh.createdAt BETWEEN :startDate AND :endDate")
    List<OrderStatusHistory> findStatusChangesInDateRange(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
} 
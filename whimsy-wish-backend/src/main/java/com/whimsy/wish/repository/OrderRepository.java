package com.whimsy.wish.repository;

import com.whimsy.wish.domain.order.Order;
import com.whimsy.wish.domain.order.OrderStatus;
import com.whimsy.wish.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByUser(User user, Pageable pageable);
    
    Page<Order> findByUserAndStatus(User user, OrderStatus status, Pageable pageable);
    
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt <= :cutoffDate")
    List<Order> findOrdersInStatusForTooLong(
            @Param("status") OrderStatus status,
            @Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND " +
           "(LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> searchUserOrders(
            @Param("userId") UUID userId,
            @Param("keyword") String keyword,
            Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Order> searchAllOrders(@Param("keyword") String keyword, Pageable pageable);
} 
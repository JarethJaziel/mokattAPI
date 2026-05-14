package com.mokatta.mokatta_api.orders;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    @Query("SELECT new com.mokatta.mokatta_api.reports.dtos.TopProductDTO(oi.productName, SUM(oi.quantity), SUM(oi.subtotal)) " +
           "FROM OrderItem oi WHERE oi.order.createdAt >= :start AND oi.order.createdAt <= :end " +
           "GROUP BY oi.productName ORDER BY SUM(oi.quantity) DESC")
    List<com.mokatta.mokatta_api.reports.dtos.TopProductDTO> getTopProducts(
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end, 
            Pageable pageable);

    @Query("SELECT new com.mokatta.mokatta_api.reports.dtos.CategoryRevenueDTO(oi.product.category.name, SUM(oi.subtotal)) " +
           "FROM OrderItem oi WHERE oi.order.createdAt >= :start AND oi.order.createdAt <= :end " +
           "GROUP BY oi.product.category.name ORDER BY SUM(oi.subtotal) DESC")
    List<com.mokatta.mokatta_api.reports.dtos.CategoryRevenueDTO> getRevenueByCategory(
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
}

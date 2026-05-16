package com.mokatta.mokatta_api.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.createdAt >= :start AND o.createdAt <= :end")
    BigDecimal getTotalSales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o.id) FROM Order o WHERE o.createdAt >= :start AND o.createdAt <= :end")
    Long getOrdersCount(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Native query to group by day. Returns [String date, BigDecimal amount]
    @Query(value = "SELECT TO_CHAR(created_at, 'YYYY-MM-DD') as day, SUM(total) as amount " +
                   "FROM orders WHERE created_at >= :start AND created_at <= :end " +
                   "GROUP BY day ORDER BY day ASC", nativeQuery = true)
    List<Object[]> getSalesSeries(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Native query to group by day. Returns [String date, Long count]
    @Query(value = "SELECT TO_CHAR(created_at, 'YYYY-MM-DD') as day, COUNT(id) as cnt " +
                   "FROM orders WHERE created_at >= :start AND created_at <= :end " +
                   "GROUP BY day ORDER BY day ASC", nativeQuery = true)
    List<Object[]> getCountSeries(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new com.mokatta.mokatta_api.reports.dtos.PaymentMethodSplitDTO(CAST(o.paymentMethod AS string), SUM(o.total), COUNT(o.id)) " +
           "FROM Order o WHERE o.createdAt >= :start AND o.createdAt <= :end " +
           "GROUP BY o.paymentMethod")
    List<com.mokatta.mokatta_api.reports.dtos.PaymentMethodSplitDTO> getPaymentMethodSplit(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Native query to group by hour. Returns [Integer hour, Long count]
    @Query(value = "SELECT CAST(EXTRACT(HOUR FROM created_at) AS INTEGER) as hr, COUNT(id) as cnt " +
                   "FROM orders WHERE created_at >= :start AND created_at <= :end " +
                   "GROUP BY hr ORDER BY hr ASC", nativeQuery = true)
    List<Object[]> getPeakHours(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}

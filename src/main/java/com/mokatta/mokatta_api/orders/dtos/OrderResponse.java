package com.mokatta.mokatta_api.orders.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        String userId,
        String userEmail,
        String paymentMethod,
        BigDecimal subtotal,
        BigDecimal total,
        String notes,
        LocalDateTime createdAt,
        List<OrderItemResponse> items) {
}

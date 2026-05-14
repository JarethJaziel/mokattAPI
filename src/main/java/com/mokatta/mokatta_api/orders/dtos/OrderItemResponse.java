package com.mokatta.mokatta_api.orders.dtos;

import java.math.BigDecimal;

public record OrderItemResponse(
        String id,
        String productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal) {
}

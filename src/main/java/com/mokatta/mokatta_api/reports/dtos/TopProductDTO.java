package com.mokatta.mokatta_api.reports.dtos;

import java.math.BigDecimal;

public record TopProductDTO(
        String productName,
        Long quantitySold,
        BigDecimal totalRevenue) {
}

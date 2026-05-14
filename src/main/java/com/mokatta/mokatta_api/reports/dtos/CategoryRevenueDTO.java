package com.mokatta.mokatta_api.reports.dtos;

import java.math.BigDecimal;

public record CategoryRevenueDTO(
        String categoryName,
        BigDecimal revenue) {
}

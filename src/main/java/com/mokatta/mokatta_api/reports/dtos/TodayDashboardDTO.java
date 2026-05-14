package com.mokatta.mokatta_api.reports.dtos;

import java.math.BigDecimal;

public record TodayDashboardDTO(
        BigDecimal totalSales,
        Long ordersCount,
        BigDecimal avgOrderValue) {
}

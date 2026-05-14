package com.mokatta.mokatta_api.reports.dtos;

import java.math.BigDecimal;

public record SalesSeriesDTO(
        String date,
        BigDecimal amount) {
}

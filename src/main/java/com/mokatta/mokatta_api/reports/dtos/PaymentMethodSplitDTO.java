package com.mokatta.mokatta_api.reports.dtos;

import java.math.BigDecimal;

public record PaymentMethodSplitDTO(
        String paymentMethod,
        BigDecimal amount,
        Long count) {
}

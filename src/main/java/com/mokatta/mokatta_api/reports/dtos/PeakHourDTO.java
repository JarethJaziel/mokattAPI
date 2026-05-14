package com.mokatta.mokatta_api.reports.dtos;

public record PeakHourDTO(
        Integer hour,
        Long ordersCount) {
}

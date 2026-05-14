package com.mokatta.mokatta_api.reports;

import com.mokatta.mokatta_api.orders.OrderItemRepository;
import com.mokatta.mokatta_api.orders.OrderRepository;
import com.mokatta.mokatta_api.reports.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public TodayDashboardDTO getTodayDashboard() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        BigDecimal totalSales = orderRepository.getTotalSales(startOfDay, endOfDay);
        if (totalSales == null) totalSales = BigDecimal.ZERO;

        Long ordersCount = orderRepository.getOrdersCount(startOfDay, endOfDay);
        if (ordersCount == null) ordersCount = 0L;

        BigDecimal avgOrderValue = BigDecimal.ZERO;
        if (ordersCount > 0) {
            avgOrderValue = totalSales.divide(BigDecimal.valueOf(ordersCount), 2, RoundingMode.HALF_UP);
        }

        return new TodayDashboardDTO(totalSales, ordersCount, avgOrderValue);
    }

    @Transactional(readOnly = true)
    public List<SalesSeriesDTO> getSalesSeries(LocalDateTime from, LocalDateTime to) {
        List<Object[]> results = orderRepository.getSalesSeries(from, to);
        return results.stream()
                .map(obj -> new SalesSeriesDTO((String) obj[0], (BigDecimal) obj[1]))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CountSeriesDTO> getOrdersCountSeries(LocalDateTime from, LocalDateTime to) {
        List<Object[]> results = orderRepository.getCountSeries(from, to);
        return results.stream()
                .map(obj -> new CountSeriesDTO((String) obj[0], ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopProductDTO> getTopProducts(LocalDateTime from, LocalDateTime to, int limit) {
        return orderItemRepository.getTopProducts(from, to, PageRequest.of(0, limit));
    }

    @Transactional(readOnly = true)
    public List<CategoryRevenueDTO> getRevenueByCategory(LocalDateTime from, LocalDateTime to) {
        return orderItemRepository.getRevenueByCategory(from, to);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodSplitDTO> getPaymentMethodSplit(LocalDateTime from, LocalDateTime to) {
        return orderRepository.getPaymentMethodSplit(from, to);
    }

    @Transactional(readOnly = true)
    public List<PeakHourDTO> getPeakHours(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Object[]> results = orderRepository.getPeakHours(startOfDay, endOfDay);
        return results.stream()
                .map(obj -> new PeakHourDTO(((Number) obj[0]).intValue(), ((Number) obj[1]).longValue()))
                .collect(Collectors.toList());
    }
}

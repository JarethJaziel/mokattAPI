package com.mokatta.mokatta_api.reports;

import com.mokatta.mokatta_api.reports.dtos.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints for generating POS charts and summaries")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/today")
    @Operation(summary = "Today's Dashboard", description = "Returns total sales, orders count, and average order value for today.")
    public ResponseEntity<TodayDashboardDTO> getTodayDashboard() {
        return ResponseEntity.ok(reportService.getTodayDashboard());
    }

    @GetMapping("/sales")
    @Operation(summary = "Sales Series", description = "Returns daily total sales for a given date range.")
    public ResponseEntity<List<SalesSeriesDTO>> getSalesSeries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(reportService.getSalesSeries(from, to));
    }

    @GetMapping("/orders-count")
    @Operation(summary = "Orders Count Series", description = "Returns daily order count for a given date range.")
    public ResponseEntity<List<CountSeriesDTO>> getOrdersCountSeries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(reportService.getOrdersCountSeries(from, to));
    }

    @GetMapping("/top-products")
    @Operation(summary = "Top Selling Products", description = "Returns top N products by quantity sold.")
    public ResponseEntity<List<TopProductDTO>> getTopProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getTopProducts(from, to, limit));
    }

    @GetMapping("/revenue-by-category")
    @Operation(summary = "Revenue by Category", description = "Returns revenue grouped by product category.")
    public ResponseEntity<List<CategoryRevenueDTO>> getRevenueByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(reportService.getRevenueByCategory(from, to));
    }

    @GetMapping("/payment-methods")
    @Operation(summary = "Sales by Payment Method", description = "Returns total sales grouped by payment method (CASH vs CARD).")
    public ResponseEntity<List<PaymentMethodSplitDTO>> getPaymentMethodSplit(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(reportService.getPaymentMethodSplit(from, to));
    }

    @GetMapping("/peak-hours")
    @Operation(summary = "Peak Hours", description = "Returns orders count grouped by hour for a specific date.")
    public ResponseEntity<List<PeakHourDTO>> getPeakHours(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getPeakHours(date));
    }

    @GetMapping("/download/pdf")
    @Operation(summary = "Download Sales Summary PDF", description = "Returns a PDF containing sales summary for a date range.")
    public ResponseEntity<Resource> downloadSalesSummaryPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        
        byte[] pdfBytes = reportService.generateSalesSummaryPdf(from, to);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_summary.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }

    @GetMapping("/download/excel")
    @Operation(summary = "Download Sales Summary Excel", description = "Returns an Excel file containing sales summary for a date range.")
    public ResponseEntity<Resource> downloadSalesSummaryExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        
        byte[] excelBytes = reportService.generateSalesSummaryExcel(from, to);
        ByteArrayResource resource = new ByteArrayResource(excelBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_summary.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excelBytes.length)
                .body(resource);
    }
}

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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mokatta.mokatta_api.orders.Order;

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

    @Transactional(readOnly = true)
    public byte[] generateSalesSummaryPdf(LocalDateTime from, LocalDateTime to) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(from, to);
        BigDecimal totalSales = orders.stream().map(Order::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Sales Summary Report", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            Paragraph subtitle = new Paragraph("From: " + from.format(formatter) + " To: " + to.format(formatter), subtitleFont);
            subtitle.setAlignment(Paragraph.ALIGN_CENTER);
            subtitle.setSpacingAfter(20f);
            document.add(subtitle);

            Paragraph summary = new Paragraph("Total Orders: " + orders.size() + "\nTotal Sales: $" + totalSales, subtitleFont);
            summary.setSpacingAfter(20f);
            document.add(summary);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{3.0f, 2.0f, 2.0f, 2.0f});

            table.addCell(getHeadCell("Order ID"));
            table.addCell(getHeadCell("Date"));
            table.addCell(getHeadCell("Payment Method"));
            table.addCell(getHeadCell("Total"));

            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (Order order : orders) {
                table.addCell(new Phrase(order.getId().toString(), cellFont));
                table.addCell(new Phrase(order.getCreatedAt().format(formatter), cellFont));
                table.addCell(new Phrase(order.getPaymentMethod().name(), cellFont));
                table.addCell(new Phrase("$" + order.getTotal().toString(), cellFont));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private PdfPCell getHeadCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        return cell;
    }

    @Transactional(readOnly = true)
    public byte[] generateSalesSummaryExcel(LocalDateTime from, LocalDateTime to) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(from, to);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sales Summary");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Order ID", "Date", "Payment Method", "Subtotal", "Total"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getId().toString());
                row.createCell(1).setCellValue(order.getCreatedAt().format(formatter));
                row.createCell(2).setCellValue(order.getPaymentMethod().name());
                row.createCell(3).setCellValue(order.getSubtotal().doubleValue());
                row.createCell(4).setCellValue(order.getTotal().doubleValue());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }
}

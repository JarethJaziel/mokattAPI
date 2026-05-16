package com.mokatta.mokatta_api.tickets;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mokatta.mokatta_api.orders.Order;
import com.mokatta.mokatta_api.orders.OrderItem;
import com.mokatta.mokatta_api.orders.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public byte[] generateTicketPdf(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Receipt sized document (e.g., thermal printer, 80mm width roughly mapped to 226 points)
            Rectangle pageSize = new Rectangle(226, 800);
            Document document = new Document(pageSize, 10, 10, 10, 10);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);

            // Header
            Paragraph header = new Paragraph("CAFÉ MOKATTA", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph address = new Paragraph("Av. Principal #123\nCiudad, Estado\nTel: 555-1234", normalFont);
            address.setAlignment(Element.ALIGN_CENTER);
            address.setSpacingAfter(10f);
            document.add(address);

            // Order Info
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            document.add(new Paragraph("Ticket: " + order.getId().toString().substring(0, 8).toUpperCase(), normalFont));
            document.add(new Paragraph("Date: " + order.getCreatedAt().format(formatter), normalFont));
            document.add(new Paragraph("Payment: " + order.getPaymentMethod().name(), normalFont));
            
            Paragraph separator = new Paragraph("----------------------------------------", normalFont);
            separator.setAlignment(Element.ALIGN_CENTER);
            document.add(separator);

            // Items Table
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 1.5f});

            table.addCell(getNoBorderCell("Qty", boldFont, Element.ALIGN_LEFT));
            table.addCell(getNoBorderCell("Item", boldFont, Element.ALIGN_LEFT));
            table.addCell(getNoBorderCell("Total", boldFont, Element.ALIGN_RIGHT));

            for (OrderItem item : order.getItems()) {
                table.addCell(getNoBorderCell(String.valueOf(item.getQuantity()), normalFont, Element.ALIGN_LEFT));
                table.addCell(getNoBorderCell(item.getProductName(), normalFont, Element.ALIGN_LEFT));
                table.addCell(getNoBorderCell("$" + item.getSubtotal(), normalFont, Element.ALIGN_RIGHT));
            }

            document.add(table);
            document.add(separator);

            // Totals
            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(100);
            totalsTable.setWidths(new float[]{3f, 1f});

            totalsTable.addCell(getNoBorderCell("SUBTOTAL:", boldFont, Element.ALIGN_RIGHT));
            totalsTable.addCell(getNoBorderCell("$" + order.getSubtotal(), normalFont, Element.ALIGN_RIGHT));

            totalsTable.addCell(getNoBorderCell("TOTAL:", boldFont, Element.ALIGN_RIGHT));
            totalsTable.addCell(getNoBorderCell("$" + order.getTotal(), boldFont, Element.ALIGN_RIGHT));

            document.add(totalsTable);

            document.add(separator);

            // Footer
            Paragraph footer = new Paragraph("¡Gracias por su compra!\nVuelva pronto.", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10f);
            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating ticket PDF", e);
        }
    }

    private PdfPCell getNoBorderCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }
}

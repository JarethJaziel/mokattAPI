package com.mokatta.mokatta_api.tickets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Public endpoints for generating order tickets (receipts)")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping(value = "/{orderId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Download Ticket PDF", description = "Returns a PDF receipt for the specified order.")
    public ResponseEntity<Resource> downloadTicketPdf(@PathVariable UUID orderId) {
        byte[] pdfBytes = ticketService.generateTicketPdf(orderId);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ticket_" + orderId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }
}

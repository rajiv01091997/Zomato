package com.zomato.invoice_service.controller;

import com.zomato.invoice_service.dto.InvoiceDto;


import com.zomato.invoice_service.servcie.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateInvoice(@RequestBody InvoiceDto invoiceDto) {
        try {
            String logoPath = "/Users/rajivyadav/Desktop/Applogo.png";

            // Generate PDF + send email
            invoiceService.generateInvoicePdf(logoPath, invoiceDto);

            return ResponseEntity.ok("Invoice generated & email sent successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating invoice");
        }
    }

}

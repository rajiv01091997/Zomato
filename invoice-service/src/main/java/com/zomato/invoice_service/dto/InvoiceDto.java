package com.zomato.invoice_service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class InvoiceDto {
    private String invoiceNumber;
    private String orderId;
    private String restaurantName;
    private String restaurantEmail;
    private String restaurantContact;
    private String restaurantAddress;
    private String customerName;
    private String customerEmail;
    private String customerContact;
    private String customerAddress;
    private String riderEmail;
    private String riderName;
    private String riderOtp;
    private List<ItemDto> items;
    private double subtotal;
    private double couponDiscount;
    private double deliveryCharge;
    private double platformFee;
    private double gstAmount;
    private double totalPayable;
    private LocalDateTime invoiceDate;
}

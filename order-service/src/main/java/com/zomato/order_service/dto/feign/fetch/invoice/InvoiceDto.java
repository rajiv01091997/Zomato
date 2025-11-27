package com.zomato.order_service.dto.feign.fetch.invoice;

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
    private List<ItemDto> items;
    private double subtotal;
    private double couponDiscount;
    private double deliveryCharge;
    private double platformFee;
    private double gstAmount;
    private double totalPayable;
    private LocalDateTime invoiceDate;
}

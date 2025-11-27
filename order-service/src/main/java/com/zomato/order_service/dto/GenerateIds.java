package com.zomato.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateIds {
    private String invoiceNumber;
    private String orderId;
}

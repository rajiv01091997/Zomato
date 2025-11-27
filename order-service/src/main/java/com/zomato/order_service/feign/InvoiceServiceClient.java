package com.zomato.order_service.feign;

import com.zomato.order_service.dto.feign.fetch.invoice.InvoiceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="invoice-service",url="localhost:8090/api/invoice")
public interface InvoiceServiceClient {

    @PostMapping("/generate")
    public void generateInvoice(@RequestBody InvoiceDto invoiceDto);
}

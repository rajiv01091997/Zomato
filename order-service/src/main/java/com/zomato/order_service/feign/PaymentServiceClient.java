package com.zomato.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="payment-service",url="localhost:8085/payments")
public interface PaymentServiceClient {

    @PostMapping("/create-link")
    public void createPaymentLink(@RequestParam("orderId") String orderId,
                                                             @RequestParam("amount") double amount,
                                                             @RequestParam("email") String email,
                                                             @RequestParam("contact") String contact);
}

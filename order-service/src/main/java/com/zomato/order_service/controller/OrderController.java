package com.zomato.order_service.controller;

import com.zomato.order_service.dto.PlaceOrderRequestDto;
import com.zomato.order_service.dto.PlaceOrderResponseDto;
import com.zomato.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService service;
    @PostMapping
    public ResponseEntity<?> place(@RequestBody PlaceOrderRequestDto requestDto)
    {
     return new ResponseEntity<>(service.place(requestDto), HttpStatus.OK);
    }
}

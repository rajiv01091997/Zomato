package com.zomato.order_service.controller;

import com.zomato.order_service.dto.PlaceOrderRequestDto;
import com.zomato.order_service.dto.UpdateStatus;
import com.zomato.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update/rider")
    ResponseEntity<?> updateStatusOfOrderByRider(@RequestBody UpdateStatus status)
    {
         return new ResponseEntity<>(service.updateRiderOrderStatus(status),HttpStatus.OK);
    }
    @PutMapping("/update/restaurant")
    ResponseEntity<?> updateStatusOfOrderByRestaurant(@RequestBody UpdateStatus status)
    {
        return new ResponseEntity<>(service.updateRestaurantOrderStatus(status),HttpStatus.OK);
    }
}

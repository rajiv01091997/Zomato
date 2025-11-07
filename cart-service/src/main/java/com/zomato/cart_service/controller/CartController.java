package com.zomato.cart_service.controller;

import com.zomato.cart_service.dto.add.AddCartRequestDto;
import com.zomato.cart_service.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> createCart(@RequestBody AddCartRequestDto addCartRequestDto)
    {
        return new ResponseEntity<>(cartService.create(addCartRequestDto), HttpStatus.CREATED);
    }

}

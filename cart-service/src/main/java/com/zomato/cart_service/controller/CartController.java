package com.zomato.cart_service.controller;

import com.zomato.cart_service.dto.add.AddCartRequestDto;
import com.zomato.cart_service.service.CartService;
import feign.Response;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCartWithGivenId(@PathVariable("cartId")UUID cartId)
    {
        return new ResponseEntity<>(cartService.deleteCart(cartId),HttpStatus.OK);
    }
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllCartsForOneCustomer()
    {
        return new ResponseEntity<>(cartService.getAllCartsForOneCustomer(),HttpStatus.OK);
    }

    @GetMapping("/getAll/active")
    public ResponseEntity<?> getAllActiveCartsForOneCustomer()
    {
        return new ResponseEntity<>(cartService.getAllActiveCartsForOneCustomer(),HttpStatus.OK);
    }

    @GetMapping("/getActive/{restaurantId}")
    public ResponseEntity<?> getActiveCartForCustomerFromGivenRestaurant(@PathVariable("restaurantId") UUID restaurantId)
    {
        return new ResponseEntity<>(cartService.getActiveCartForCustomerFromGivenRestaurant(restaurantId),HttpStatus.OK);
    }




}

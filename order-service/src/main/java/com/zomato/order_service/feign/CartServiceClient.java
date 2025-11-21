package com.zomato.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name="cart-service",url="localhost:8082/api/cart")
public interface CartServiceClient {

    @GetMapping("/isPresent/{cartId}")
    public boolean isPresentWithCartId(@PathVariable("cartId") UUID cartId);


    @GetMapping("/isActive/{cartId}")
    public boolean isActive(@PathVariable("cartId") UUID cartId);

    @GetMapping("/isSameAmount/{cartId}")
    public boolean isSameCurrentAmountFromMenuToCartAmount(@PathVariable("cartId") UUID cartId);


    @GetMapping("/getCustomerId/{cartId}")
    public UUID getCustomerIdWithCartId(@PathVariable("cartId") UUID cartId);

    @GetMapping("/getCouponCode/{cartId}")
    public String getCouponCode(@PathVariable("cartId") UUID cartId);

    @GetMapping("/isCouponApplied/{cartId}")
    public boolean isCouponAppliedOnCart(@PathVariable("cartId") UUID cartId);

    @GetMapping("/getRestaurantId/{cartId}")
    public UUID getRestaurantId(@PathVariable("cartId") UUID cartId);

    @GetMapping("/getTotalAmount/{cartId}")
    public double getTotalAmount(@PathVariable("cartId") UUID cartId);

}

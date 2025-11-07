package com.zomato.restaurant_service.controller;

import com.zomato.restaurant_service.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/get/restaurants")
    public ResponseEntity<?> getRestaurantList()
    {
        return new ResponseEntity<>(restaurantService.getRestaurantList(), HttpStatus.OK);
    }
}

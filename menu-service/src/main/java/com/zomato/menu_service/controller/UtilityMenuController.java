package com.zomato.menu_service.controller;

import com.zomato.menu_service.service.UtilityMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
public class UtilityMenuController {
      @Autowired
      private UtilityMenuService menuService;
    //method to be used in cart-service
    @GetMapping("/get/restaurantId/{itemId}")
    public ResponseEntity<?> getRestaurantIdByItemId(@PathVariable("itemId") UUID itemId)
    {
        return new ResponseEntity<>(menuService.getRestaurantIdByItemId(itemId), HttpStatus.OK);
    }
    @GetMapping("/get/price/{itemId}")
    public ResponseEntity<?> getPriceByItemId(@PathVariable("itemId") UUID itemId)
    {
        return new ResponseEntity<>(menuService.getPriceByItemId(itemId),HttpStatus.OK);
    }
    @GetMapping("/get/availability/{itemId}")
    public ResponseEntity<?> getAvailabilityByItemId(@PathVariable("itemId") UUID itemId)
    {
        return new ResponseEntity<>(menuService.getAvailabilityByItemId(itemId),HttpStatus.OK);
    }
    @GetMapping("/get/itemName/{itemId}")
    public ResponseEntity<?> getItemNameByItemId(@PathVariable("itemId") UUID itemId)
    {
        return new ResponseEntity<>(menuService.getItemNameByItemId(itemId),HttpStatus.OK);
    }
    @GetMapping("get/allItems/{restaurantId}")
    public ResponseEntity displayAllItemsForGivenRestaurant(@PathVariable("restaurantId") UUID restaurantId)
    {
        return new ResponseEntity(menuService.displayAllItemsForGivenRestaurant(restaurantId),HttpStatus.OK);
    }

}

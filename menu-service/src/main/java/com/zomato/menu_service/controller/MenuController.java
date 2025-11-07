package com.zomato.menu_service.controller;

import com.zomato.menu_service.dto.AddToMenuRequestDto;
import com.zomato.menu_service.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestBody AddToMenuRequestDto addToMenuRequestDto)
    {
        return new ResponseEntity<>(menuService.addItemToMenu(addToMenuRequestDto), HttpStatus.CREATED);
    }

    //method to be used in cart-service
    @GetMapping("/get/restaurantId/{itemId}")
    public ResponseEntity<?> getRestaurantIdByItemId(@PathVariable("itemId") UUID itemId)
    {
        return new ResponseEntity<>(menuService.getRestaurantIdByItemId(itemId),HttpStatus.OK);
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

}

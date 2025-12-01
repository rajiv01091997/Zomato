package com.zomato.menu_service.controller;

import com.zomato.menu_service.service.UtilityMenuService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("get/AllFiltered/{restaurantId}")
    public ResponseEntity<?> filterMenu(@RequestParam(value="course",defaultValue = "ALL") String course,
                                        @RequestParam(value="kind",defaultValue = "ALL") String kind,
                                        @PathVariable("restaurantId") UUID restaurantId)
    {
        return new ResponseEntity<>(menuService.filter(course,kind,restaurantId),HttpStatus.OK);
    }
}

package com.zomato.menu_service.controller;

import com.zomato.menu_service.dto.AddToMenuRequestDto;
import com.zomato.menu_service.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

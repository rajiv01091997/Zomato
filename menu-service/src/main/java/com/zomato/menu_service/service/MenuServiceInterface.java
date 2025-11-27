package com.zomato.menu_service.service;

import com.zomato.menu_service.dto.add.AddToMenuRequestDto;
import com.zomato.menu_service.dto.add.AddToMenuResponseDto;

public interface MenuServiceInterface {
    AddToMenuResponseDto addItemToMenu(AddToMenuRequestDto addToMenuRequestDto);
}

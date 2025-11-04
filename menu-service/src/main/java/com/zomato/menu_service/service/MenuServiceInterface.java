package com.zomato.menu_service.service;

import com.zomato.menu_service.dto.AddToMenuRequestDto;
import com.zomato.menu_service.dto.AddToMenuResponseDto;

public interface MenuServiceInterface {
    AddToMenuResponseDto addItemToMenu(AddToMenuRequestDto addToMenuRequestDto);
}

package com.zomato.menu_service.service;

import com.zomato.menu_service.dto.display.DisplayMenuItem;
import com.zomato.menu_service.entity.Menu;
import com.zomato.menu_service.repository.MenuRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UtilityMenuService {
    @Autowired
    private MenuRepository repository;

    @Autowired
    private ModelMapper mapper;

    //utility method for cart-service
    public Optional<UUID> getRestaurantIdByItemId(UUID itemId)
    {
        Optional<Menu> menu= repository.findById(itemId);
        if(menu.isPresent())
            return Optional.of(menu.get().getRestaurantId());
        else
            return Optional.ofNullable(null);
    }
    public Optional<Double> getPriceByItemId(UUID itemId)
    {
        Optional<Menu> menu= repository.findById(itemId);
        if(menu.isPresent())
            return Optional.of(menu.get().getPrice());
        else
            return Optional.ofNullable(null);
    }

    public Optional<Boolean> getAvailabilityByItemId(UUID itemId)
    {
        Optional<Menu> menu= repository.findById(itemId);
        if(menu.isPresent())
            return Optional.of(menu.get().isAvailable());
        else
            return Optional.ofNullable(null);
    }

    public Optional<String> getItemNameByItemId(UUID itemId)
    {
        Optional<Menu> menu= repository.findById(itemId);
        if(menu.isPresent())
            return Optional.of(menu.get().getItemName());
        else
            return Optional.ofNullable(null);
    }
    public List<DisplayMenuItem> displayAllItemsForGivenRestaurant(@PathVariable("restaurantId") UUID restaurantId)
    {
       List<Menu> list= repository.findAllByRestaurantId(restaurantId);
       List<DisplayMenuItem> displayMenuItems=new ArrayList<>();
       for(Menu item:list)
       {
           displayMenuItems.add(mapper.map(item, DisplayMenuItem.class));
       }
       return displayMenuItems;
    }
}

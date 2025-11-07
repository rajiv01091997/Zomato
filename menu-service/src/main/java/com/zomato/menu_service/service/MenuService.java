package com.zomato.menu_service.service;

import com.zomato.menu_service.dto.AddToMenuRequestDto;
import com.zomato.menu_service.dto.AddToMenuResponseDto;
import com.zomato.menu_service.entity.Menu;
import com.zomato.menu_service.repository.MenuRepository;
import com.zomato.menu_service.security.CustomPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class MenuService implements MenuServiceInterface{
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    @PreAuthorize("hasRole('RESTAURANT_MANAGER')")
    public AddToMenuResponseDto addItemToMenu(AddToMenuRequestDto addToMenuRequestDto)
    {

        CustomPrincipal currentUser= (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Added by:{}",currentUser.getUserName());

        if(!currentUser.getStatus().equals("ACTIVE"))
            throw new RuntimeException("User not active,please contact Admin");


        Menu menu=mapper.map(addToMenuRequestDto,Menu.class);
        //same item cannot be repeated twice for same restaurant
        Optional<Menu> item=menuRepository.findByItemNameAndRestaurantId(menu.getItemName(),currentUser.getId());
           if(item.isPresent())
               throw new RuntimeException("Item already in Menu, for editing use update option");

        menu.setRestaurantId(currentUser.getId());
        Menu savedMenu=menuRepository.save(menu);
        return mapper.map(menu, AddToMenuResponseDto.class);
    }

    //public List<Menu> findAllByRestaurant

    //utility method for cart-service
    public Optional<UUID> getRestaurantIdByItemId(UUID itemId)
    {
       Optional<Menu> menu= menuRepository.findById(itemId);
       if(menu.isPresent())
               return Optional.of(menu.get().getRestaurantId());
       else
           return Optional.ofNullable(null);
    }
    public Optional<Double> getPriceByItemId(UUID itemId)
    {
        Optional<Menu> menu= menuRepository.findById(itemId);
        if(menu.isPresent())
            return Optional.of(menu.get().getPrice());
        else
            return Optional.ofNullable(null);
    }

    public Optional<Boolean> getAvailabilityByItemId(UUID itemId)
    {
        Optional<Menu> menu= menuRepository.findById(itemId);
        if(menu.isPresent())
            return Optional.of(menu.get().isAvailable());
        else
            return Optional.ofNullable(null);
    }

    public Optional<String> getItemNameByItemId(UUID itemId)
    {
        Optional<Menu> menu= menuRepository.findById(itemId);
        if(menu.isPresent())
            return Optional.of(menu.get().getItemName());
        else
            return Optional.ofNullable(null);
    }
}

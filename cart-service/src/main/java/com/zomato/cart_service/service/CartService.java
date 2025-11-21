package com.zomato.cart_service.service;


import com.zomato.cart_service.dto.add.AddCartRequestDto;
import com.zomato.cart_service.dto.add.AddCartResponseDto;
import com.zomato.cart_service.dto.add.AddItemListDto;
import com.zomato.cart_service.entity.Cart;
import com.zomato.cart_service.entity.Item;
import com.zomato.cart_service.enums.CartStatus;
import com.zomato.cart_service.exceptions.ActiveCartDuplicacyException;
import com.zomato.cart_service.feign.MenuServiceClient;
import com.zomato.cart_service.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CartService implements CartServiceInterface{

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MenuServiceClient menuServiceClient;//feign client for interacting menu-service

    public AddCartResponseDto create(AddCartRequestDto addCartRequestDto)
    {
        //if an active cart associated with same restaurant and customer present ask to edit same or delete
         if(cartRepository.findCartByCustomerIdAndRestaurantIdAndStatus(addCartRequestDto.getCustomerId(),
                 addCartRequestDto.getRestaurantId(),CartStatus.ACTIVE).isPresent())
             throw new ActiveCartDuplicacyException("You already have a active cart associated with this restaurant. Either update same cart or Delete it and start afresh ");
        //change dto to entity
       Cart cart=new Cart();
       cart.setRestaurantId(addCartRequestDto.getRestaurantId());
       cart.setCustomerId(addCartRequestDto.getCustomerId());

        if(addCartRequestDto.getAddItemListDto()!=null) {
            List<Item> itemList=new ArrayList<>();
            for (AddItemListDto item : addCartRequestDto.getAddItemListDto()) {
                 itemList.add(modelMapper.map(item, Item.class));
            }
            cart.setItemList(itemList);
        }
        else
            throw  new RuntimeException("Cart cannot be created without any Items, Please add Items and try");
         //before creating a cart check if user has sent all items from same restaurant or not
        //check if all items of cart available
        //then calculate total cart amount
        UUID restaurantId=cart.getRestaurantId();
        double total=0;
        for(Item item:cart.getItemList())
        {
            if (!menuServiceClient.getRestaurantIdByItemId(item.getItemId()).get().equals(restaurantId))
                throw new RuntimeException("Items are not from same restaurant,add items from single restaurant only ");
            if(menuServiceClient.getAvailabilityByItemId(item.getItemId()).get()==false)
                throw new RuntimeException("Item: "+item.getItemId()+" not available at the moment, Please select available items");
            else
                total+=menuServiceClient.getPriceByItemId(item.getItemId()).get()* item.getQuantity();
        }

        /*
        for coupon deductions add logic here to deduct discount amount from total
         */

        cart.setStatus(CartStatus.ACTIVE);
        cart.setCouponCode("");//later fetch from coupon service
        cart.setTotalAmount(total);
         //set cart in all the items
        for(Item item:cart.getItemList())
        {
            item.setCart(cart);
        }
        Cart savedCart=cartRepository.save(cart);

        AddCartResponseDto responseDto=modelMapper.map(savedCart,AddCartResponseDto.class);
        List<AddItemListDto> list=new ArrayList<>();
        for(Item item:savedCart.getItemList())
        {
            AddItemListDto dto=modelMapper.map(item,AddItemListDto.class);
            dto.setPrice(menuServiceClient.getPriceByItemId(dto.getItemId()).get());
            dto.setItemName(menuServiceClient.getItemNameByItemId(dto.getItemId()).get());
            log.info(menuServiceClient.getItemNameByItemId(dto.getItemId()).get());
           list.add(dto);
        }
        responseDto.setAddItemListDto(list);
        return responseDto;
        //itemNames are coming enclosed by \and \ fix it
    }
    //update cart method to be implemented(allow to edit only active cart)
    //delete cart method to be implemented(can delete any)
    //show all(non-active) carts associated with a restaurant(for restaurant_manager)
    //show all carts for a customer
    //show active cart contents


}

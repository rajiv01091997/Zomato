package com.zomato.cart_service.service;

import com.zomato.cart_service.dto.feign.expose.CartBridgeDto;
import com.zomato.cart_service.dto.feign.expose.ItemBridgeDto;
import com.zomato.cart_service.entity.Cart;
import com.zomato.cart_service.entity.Item;
import com.zomato.cart_service.enums.CartStatus;
import com.zomato.cart_service.feign.MenuServiceClient;
import com.zomato.cart_service.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UtilityCartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private MenuServiceClient menuServiceClient;//feign client for interacting menu-service

    //for order-service to access
    public boolean isPresentWithCartId(UUID cartId)
    {
        Cart cart=cartRepository.findById(cartId).get();
        if(cart==null)
            return false;
        else
            return true;
    }
    public UUID getCustomerIdWithCartId(UUID cartId)
    {
        return cartRepository.findById(cartId).get().getCustomerId();
    }
    public boolean isActive(UUID cartId)
    {
        Cart cart=cartRepository.findById(cartId).get();
        if(cart.getStatus()== CartStatus.ACTIVE)
            return true;
        else
            return false;
    }
    public boolean isSameCurrentAmountFromMenuToCartAmount(UUID cartId)
    {
        Cart cart=cartRepository.findById(cartId).get();
        double currentTotal=0;
        for(Item item:cart.getItemList())
        {
            currentTotal+=menuServiceClient.getPriceByItemId(item.getItemId()).get();
        }
        if(currentTotal!=cart.getTotalAmount())
            return false;
        else
            return true;
    }
    public boolean isCouponAppliedOnCart(UUID cartId)
    {
        Cart cart=cartRepository.findById(cartId).get();
        if(cart.getCouponCode()==null)
            return false;
        else
            return true;
    }
    public String getCouponCode(UUID cartId)
    {
        Cart cart=cartRepository.findById(cartId).get();
        return cart.getCouponCode();
    }
    public UUID getRestaurantId(UUID cartId)
    {
      return  cartRepository.findById(cartId).get().getRestaurantId();
    }
    public double getTotalAmount(UUID cartId)
    {
        return cartRepository.findById(cartId).get().getTotalAmount();
    }

    public CartBridgeDto getCartDetails(UUID cartId)
    {
        Cart cart=cartRepository.findById(cartId).get();
        if(cart==null)
            return null;

        CartBridgeDto cartBridgeDto=new CartBridgeDto();
        cartBridgeDto.setCartId(cart.getCartId());
        cartBridgeDto.setCustomerId(cart.getCustomerId());
        cartBridgeDto.setStatus(cart.getStatus());
        cartBridgeDto.setRestaurantId(cart.getRestaurantId());
        cartBridgeDto.setTotalAmount(cart.getTotalAmount());
        cartBridgeDto.setCouponCode(cart.getCouponCode());
        cartBridgeDto.setCreatedAt(cart.getCreatedAt());
        cartBridgeDto.setUpdatedAt(cart.getUpdatedAt());
        List<ItemBridgeDto> list=new ArrayList<>();
        for(Item item:cart.getItemList())
        {
            ItemBridgeDto bridgeDto=new ItemBridgeDto();
            bridgeDto.setPrice(menuServiceClient.getPriceByItemId(item.getItemId()).get());
            bridgeDto.setQuantity(item.getQuantity());
            bridgeDto.setItemName(menuServiceClient.getItemNameByItemId(item.getItemId()).get());
            bridgeDto.setItemId(item.getItemId());
            list.add(bridgeDto);
        }
        cartBridgeDto.setItemBridgeDtoList(list);
        return cartBridgeDto;
    }

}

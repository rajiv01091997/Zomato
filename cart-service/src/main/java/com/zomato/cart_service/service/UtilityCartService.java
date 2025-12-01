package com.zomato.cart_service.service;

import com.zomato.cart_service.dto.feign.expose.CartBridgeDto;
import com.zomato.cart_service.dto.feign.expose.ItemBridgeDto;
import com.zomato.cart_service.dto.feign.fetch.CouponBridgeDto;
import com.zomato.cart_service.dto.feign.fetch.DiscountType;
import com.zomato.cart_service.entity.Cart;
import com.zomato.cart_service.entity.Item;
import com.zomato.cart_service.enums.CartStatus;
import com.zomato.cart_service.feign.CouponServiceClient;
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
    @Autowired
    private CouponServiceClient couponServiceClient;
    //for order-service to access


    public boolean isSameCurrentAmountFromMenuToCartAmount(UUID cartId)
    {
        Cart cart=cartRepository.findById(cartId).get();
        if(cart==null)
            throw new RuntimeException("No cart with given id");
        double currentTotal=0;
        for(Item item:cart.getItemList())
        {
            currentTotal+=menuServiceClient.getPriceByItemId(item.getItemId()).get()* item.getQuantity();
        }
        //No need of checking after adding coupon just check grossAmount;
//        if(cart.getCouponCode()!=null) {
//            CouponBridgeDto couponBridgeDto = couponServiceClient.getCouponWithCouponCodeAndRestaurant(cart.getCouponCode(), cart.getRestaurantId());
//            double discountValue = couponBridgeDto.getDiscountValue();
//            if (couponBridgeDto.getDiscountType() == DiscountType.FLAT) {
//                currentTotal -= discountValue;
//            } else {
//                double amount = (discountValue * currentTotal) / 100;
//                currentTotal -= amount;
//            }
//        }
        log.info("gross amount as per current pricing:{}",currentTotal);
        log.info("gross previous amount:{}",cart.getGrossAmount());
        if(currentTotal!=cart.getGrossAmount()) {

            return false;
        }
        else
            return true;
    }
    public void changeStatus(UUID cartId)
    {
        Cart cart=cartRepository.findById(cartId).get();
        if(cart==null)
            throw new RuntimeException("No cart with given id");
        log.info("changing status of cart from ACTIVE to CHECKED_OUT");
        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);
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
        cartBridgeDto.setGrossAmount(cart.getGrossAmount());
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

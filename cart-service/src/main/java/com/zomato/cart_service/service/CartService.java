package com.zomato.cart_service.service;


import com.zomato.cart_service.dto.add.AddCartRequestDto;
import com.zomato.cart_service.dto.add.AddCartResponseDto;
import com.zomato.cart_service.dto.add.AddItemDto;
import com.zomato.cart_service.dto.display.DisplayCartForOneCustomerDto;
import com.zomato.cart_service.dto.display.DisplayItemDto;
import com.zomato.cart_service.dto.feign.fetch.CouponBridgeDto;
import com.zomato.cart_service.dto.feign.fetch.DiscountType;
import com.zomato.cart_service.dto.update.UpdateCartRequestDto;
import com.zomato.cart_service.dto.update.UpdateCartResponseDto;
import com.zomato.cart_service.dto.update.UpdateItemDto;
import com.zomato.cart_service.entity.Cart;
import com.zomato.cart_service.entity.Item;
import com.zomato.cart_service.enums.CartStatus;
import com.zomato.cart_service.exceptions.ActiveCartDuplicacyException;
import com.zomato.cart_service.feign.CouponServiceClient;
import com.zomato.cart_service.feign.MenuServiceClient;
import com.zomato.cart_service.repository.CartRepository;
import com.zomato.cart_service.security.CustomPrincipal;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private CouponServiceClient couponServiceClient;

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public AddCartResponseDto create(AddCartRequestDto addCartRequestDto) {
        //if an active cart associated with same restaurant and customer present ask to edit same or delete
        //fetched customerId from jwt
        UUID customerId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if (cartRepository.findCartByCustomerIdAndRestaurantIdAndStatus(customerId,
                addCartRequestDto.getRestaurantId(), CartStatus.ACTIVE).isPresent())
            throw new ActiveCartDuplicacyException("You already have a active cart associated with this restaurant. Either update same cart or Delete it and start afresh ");
        //change dto to entity
        Cart cart = new Cart();
        cart.setRestaurantId(addCartRequestDto.getRestaurantId());
        //later fetch customerId  from jwt
        cart.setCustomerId(customerId);

        if (addCartRequestDto.getAddItemListDto() != null) {
            List<Item> itemList = new ArrayList<>();
            for (AddItemDto item : addCartRequestDto.getAddItemListDto()) {
                itemList.add(modelMapper.map(item, Item.class));
            }
            cart.setItemList(itemList);
        } else
            throw new RuntimeException("Cart cannot be created without any Items, Please add Items and try");
        //before creating a cart check if user has sent all items from same restaurant or not
        //check if all items of cart available
        //then calculate total cart amount
        UUID restaurantId = cart.getRestaurantId();
        double total = 0;
        for (Item item : cart.getItemList()) {
            if (!menuServiceClient.getRestaurantIdByItemId(item.getItemId()).get().equals(restaurantId))
                throw new RuntimeException("Items are not from same restaurant,add items from single restaurant only ");
            if (menuServiceClient.getAvailabilityByItemId(item.getItemId()).get() == false)
                throw new RuntimeException("Item: " + item.getItemId() + " not available at the moment, Please select available items");
            else
                total += menuServiceClient.getPriceByItemId(item.getItemId()).get() * item.getQuantity();
        }
        double grossAmount=total;
        log.info("grossAmount:{}",grossAmount);
        log.info("totalAmount:{}",total);
        //check whether coupon is applied on this cart or not
        String couponCode = addCartRequestDto.getCouponCode();
        if (couponCode != null) {
            //check if applied coupon code is still present in coupon-service for given restaurantId
            CouponBridgeDto couponBridgeDto = couponServiceClient.getCouponWithCouponCodeAndRestaurant(couponCode, restaurantId);
            if (couponBridgeDto == null)
                throw new RuntimeException("Applied coupon doesn't exist anymore, maybe it is removed, try other relevant coupons or make cart without coupon and proceed");
            //get couponId for given couponcode and restaurantId
            UUID couponId = couponBridgeDto.getId();
            //check coupon is active or not
            if (!couponBridgeDto.getIsActive())
                throw new RuntimeException("Coupon is not active anymore, try other coupon or proceed by creating a cart without coupon");

            //check if coupon maxusage limit is greater than current usage
            //else ask to remove coupon in cart or add other coupons and continue
            if ((couponBridgeDto.getCurrentUsageCount() >= couponBridgeDto.getOverallUsageCount()))
                throw new RuntimeException("usage limit reached for coupon, try other or proceed without coupon");
            //check if total amount greater or equal to minimumAmount required for coupon
            //else ask to apply eligible coupon in cart as per amount or update cart without coupon and continue
            if (total < couponBridgeDto.getMinOrderValue()) {
                double lag = couponBridgeDto.getMinOrderValue() - total;
                throw new RuntimeException("Minimum order value not attained to apply this coupon, add more items of value: " + lag);
            }
            //apply discount
            double discountValue = couponBridgeDto.getDiscountValue();
            if (couponBridgeDto.getDiscountType() == DiscountType.FLAT) {
                total -= discountValue;
            } else {
                double amount = Math.round(((discountValue * total) / 100)*1000.0)/1000.0;
                total -= amount;
            }
            log.info("discont baad grossAmount:{}",grossAmount);
            log.info("discount baad totalAmount:{}",total);
            //update currentUsage
            boolean success=couponServiceClient.updateCurrentUsage(couponId);
            if(!success)
                throw new RuntimeException("usage limit reached for coupon, try other or proceed without coupon");

        }

        cart.setStatus(CartStatus.ACTIVE);
        cart.setCouponCode(addCartRequestDto.getCouponCode());
        cart.setGrossAmount(grossAmount);
        cart.setTotalAmount(total);
         //set cart in all the items
        for(Item item:cart.getItemList())
        {
            item.setCart(cart);
        }
        Cart savedCart=cartRepository.save(cart);

        AddCartResponseDto responseDto=modelMapper.map(savedCart,AddCartResponseDto.class);
        List<AddItemDto> list=new ArrayList<>();
        for(Item item:savedCart.getItemList())
        {
            AddItemDto dto=modelMapper.map(item, AddItemDto.class);
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
    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public UpdateCartResponseDto updateCart(UpdateCartRequestDto updateDto)
    {
        UUID customerId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

      if(!cartRepository.findById(updateDto.getCartId()).isPresent())
          throw new RuntimeException("cart with this id not present");
        Cart cart=cartRepository.findById(updateDto.getCartId()).get();

       if(!customerId.equals(cart.getCustomerId()))
          throw new RuntimeException("You don't have ownership of this cart, please check cartId properly");

      UUID restaurantId=cart.getRestaurantId();
      //delete that cart
        deleteCart(updateDto.getCartId());
        log.info("old cart deleted");
      AddCartRequestDto addDto=modelMapper.map(updateDto,AddCartRequestDto.class);
      addDto.setRestaurantId(restaurantId);
      List<AddItemDto> addDtoList=new ArrayList<>();
      for(UpdateItemDto item:updateDto.getUpdateItemDtos())
      {
         addDtoList.add(modelMapper.map(item,AddItemDto.class));
      }
      addDto.setAddItemListDto(addDtoList);
      log.info("calling create cart after deleting the old one associated with the id");
      AddCartResponseDto addResponseDto=create(addDto);

        UpdateCartResponseDto responseDto=modelMapper.map(addResponseDto,UpdateCartResponseDto.class);
        List<UpdateItemDto> updateItemDtos=new ArrayList<>();
        for(AddItemDto item:addResponseDto.getAddItemListDto())
        {
            updateItemDtos.add(modelMapper.map(item,UpdateItemDto.class));
        }
        responseDto.setUpdateItemDtos(updateItemDtos);
        return responseDto;

    }

    //delete cart method to be implemented(can delete any)
    //for customer
    @PreAuthorize("hasRole('CUSTOMER')")
    public String deleteCart(UUID id)
    {
        //fetched this from jwt
        UUID customerId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if(cartRepository.findById(id).isPresent())
        {
            if(!cartRepository.findById(id).get().getCustomerId().equals(customerId))
                throw new RuntimeException("You are not authorised to delete this cart, check your cart id");
            cartRepository.deleteById(id);
            return "cart deleted with id: " + id;
        }
        throw new RuntimeException("No cart associated with id: "+id);
    }
    //show all carts for a customer
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<DisplayCartForOneCustomerDto> getAllCartsForOneCustomer()
    {
        //fetched this from jwt
        UUID customerId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<Cart> list=cartRepository.findAllByCustomerId(customerId);
        List<DisplayCartForOneCustomerDto> displayList=new ArrayList<>();
        for(Cart cart:list)
        {
           DisplayCartForOneCustomerDto tempCart=modelMapper.map(cart, DisplayCartForOneCustomerDto.class);
           List<DisplayItemDto> displayItemList=new ArrayList<>();
           for(Item item:cart.getItemList())
           {
              displayItemList.add(modelMapper.map(item, DisplayItemDto.class));
           }
           tempCart.setDisplayItemList(displayItemList);
           displayList.add(tempCart);
        }
        return displayList;
    }
    //show all the active carts for a customer
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<DisplayCartForOneCustomerDto> getAllActiveCartsForOneCustomer()
    {
        //fetched this from jwt
        UUID customerId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<Cart> list=cartRepository.findAllByCustomerIdAndStatus(customerId,CartStatus.ACTIVE);
        List<DisplayCartForOneCustomerDto> displayList=new ArrayList<>();
        for(Cart cart:list)
        {
            DisplayCartForOneCustomerDto tempCart=modelMapper.map(cart, DisplayCartForOneCustomerDto.class);
            List<DisplayItemDto> displayItemList=new ArrayList<>();
            for(Item item:cart.getItemList())
            {
                displayItemList.add(modelMapper.map(item, DisplayItemDto.class));
            }
            tempCart.setDisplayItemList(displayItemList);
            displayList.add(tempCart);
        }
        return displayList;
    }
    //get active cart if present for the customer from particular restaurant
    @PreAuthorize("hasRole('CUSTOMER')")
    public DisplayCartForOneCustomerDto getActiveCartForCustomerFromGivenRestaurant(UUID restaurantId)
    {
        //fetched this from jwt
        UUID customerId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Cart cart=cartRepository.findCartByCustomerIdAndRestaurantIdAndStatus(customerId,restaurantId,CartStatus.ACTIVE).get();
        if(cart==null)
            throw new RuntimeException("No Active Cart for you associated with this restaurant");
        DisplayCartForOneCustomerDto displayCartForOneCustomerDto=modelMapper.map(cart,DisplayCartForOneCustomerDto.class);
        List<DisplayItemDto> list=new ArrayList<>();
        for(Item item:cart.getItemList())
        {
           list.add(modelMapper.map(item,DisplayItemDto.class));
        }
        displayCartForOneCustomerDto.setDisplayItemList(list);
        return displayCartForOneCustomerDto;
    }


}

package com.zomato.order_service.service;


import com.zomato.order_service.dto.PlaceOrderRequestDto;
import com.zomato.order_service.dto.PlaceOrderResponseDto;
import com.zomato.order_service.feign.CartServiceClient;
import com.zomato.order_service.feign.CouponServiceClient;
import com.zomato.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService implements OrderServiceInterface {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartServiceClient cartServiceClient;
    @Autowired
    private CouponServiceClient couponServiceClient;


    public PlaceOrderResponseDto place(PlaceOrderRequestDto requestDto)
    {
        //fetch from JWT
        UUID ownerId= UUID.fromString("jbfjjbfjfefjejfefjefee");
        UUID restaurantId=cartServiceClient.getRestaurantId(requestDto.getCartId());
        //check if cart with given cartId is present or not
         if(!cartServiceClient.isPresentWithCartId(requestDto.getCartId()))
             throw new RuntimeException("No Such Cart with given cartId exist");

        //compare the ownerId with customerId from cart if this cart is his or not
         if(cartServiceClient.getCustomerIdWithCartId(requestDto.getCartId())!=ownerId)
             throw new RuntimeException("You are not the owner of this cart, please check your cartId");
        //check if cart is active or not
         if(!cartServiceClient.isActive(requestDto.getCartId()))
             throw new RuntimeException("That's not active, check correct cartId or make another Cart if you don't have a active cart");
        //compare each items price in cart with current price in menu
        //if okay then proceed else ask to re-update cart with latest prices
         if(!cartServiceClient.isSameCurrentAmountFromMenuToCartAmount(requestDto.getCartId()))
             throw new RuntimeException("Pricing for item/items have changed since you added them to your cart \n Either delete this cart and make new one or remove items and add them again");
         //get total amount
          double totalAmount=cartServiceClient.getTotalAmount(requestDto.getCartId()));
         //check if any coupon applied on this cart
        String couponCode=null;//if present get couponCode
        if(cartServiceClient.isCouponAppliedOnCart(requestDto.getCartId()))
            couponCode= cartServiceClient.getCouponCode(requestDto.getCartId());
        //if coupon is applied
         if(couponCode!=null)
         {
        //check if coupon code is still present in coupon-service for given restaurantId
          if(!couponServiceClient.isCouponWithGivenCodePresentForGivenRestaurant(couponCode,restaurantId))
              throw new RuntimeException("Applied coupon doesn't exist anymore, maybe it is removed, try other relevant coupons or make cart without coupon and proceed");
       //get couponId for given couponcode and restaurantId
          UUID couponId=couponServiceClient.getIdOfCouponWithGivenCouponCodeAndRestaurantId(couponCode,restaurantId);
             //check coupon is active or not
          if(!couponServiceClient.isCouponWithGivenIdActive(couponId))
              throw new RuntimeException("Coupon is not active anymore, try other coupon or proceed by creating a cart without coupon");


        //check if coupon maxusage limit is greater than current usage
        //else ask to remove coupon in cart or add other coupons and continue
             if(!couponServiceClient.IsOverallUsageGreaterThanCurrentUsageForGivenCoupon(couponId))
                 throw new RuntimeException("usage limit reached for coupon, try other or proceed without coupon");
//               //check if total amount greater or equal to minimumAmount required for coupon
//               //else ask to apply eligible coupon in cart as per amount or update cart without coupon and continue
//             if(!couponServiceClient.isCouponAllowedOnGivenAmount(couponId))
//                 throw new RuntimeException("You have insufficient total to apply this coupon, add more items in cart");
        //apply discount
        double discountValue= couponServiceClient.getDiscountValue(couponId);
        if(couponServiceClient.getDicountType(couponId).equals("FLAT"))
        {
            totalAmount-=discountValue;
        }
        else
        {
           double amount=(discountValue*totalAmount)/100;
           totalAmount-=amount;
        }
        //send for payment if received FAILED then save into repo with FAILED status
        //also populate other relevant fields before saving

        //if SUCCESS payment then populate relevant fields and save order

        //notify restaurant
        //notify riders
        return null;
    }

    public
}

package com.zomato.coupon_service.service;

import com.zomato.coupon_service.dto.addCoupon.AddCouponRequestDto;
import com.zomato.coupon_service.dto.addCoupon.AddCouponResponseDto;
import com.zomato.coupon_service.dto.updateCoupon.UpdateCouponRequestDto;
import com.zomato.coupon_service.dto.updateCoupon.UpdateCouponResponseDto;
import com.zomato.coupon_service.entity.Coupon;
import com.zomato.coupon_service.repository.CouponRepository;
import com.zomato.coupon_service.security.CustomPrincipal;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CouponService implements CouponServiceInterface{
    @Autowired
    private CouponRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasRole('RESTAURANT_MANAGER')")
    @Override
    public AddCouponResponseDto save(AddCouponRequestDto addCouponRequestDto) {
        //restaurantId from JWT
        UUID restaurantId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        //check if coupon with same code is present and active for this restaurant
        if(repository.findByCouponCodeAndRestaurantIdAndIsActive(addCouponRequestDto.getCouponCode(),
                restaurantId,addCouponRequestDto.getIsActive()).isPresent())
            throw new RuntimeException("A coupon with this code already present and active");

        Coupon coupon=modelMapper.map(addCouponRequestDto,Coupon.class);
        coupon.setRestaurantId(restaurantId);
        coupon.setCurrentUsageCount(0L);

        Coupon savedCoupon=repository.save(coupon);
        AddCouponResponseDto addCouponResponseDto=modelMapper.map(savedCoupon,AddCouponResponseDto.class);
        return addCouponResponseDto;
    }
    @PreAuthorize("hasRole('RESTAURANT_MANAGER')")
    @Override
    public UpdateCouponResponseDto update(UpdateCouponRequestDto updateCouponRequestDto) {
        //restaurantId from JWT
        UUID restaurantId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Coupon coupon=repository.findById(updateCouponRequestDto.getId()).
                orElseThrow(()->new RuntimeException("No coupon with this id present,please check id and try"));

        if(!coupon.getRestaurantId().equals(restaurantId))
            throw new RuntimeException("This coupon doesn't belongs to you, check id properly");

        coupon.setCouponCode(updateCouponRequestDto.getCouponCode());
        coupon.setOverallUsageCount(updateCouponRequestDto.getOverallUsageCount());
        coupon.setDiscountType(updateCouponRequestDto.getDiscountType());
        coupon.setDiscountValue(updateCouponRequestDto.getDiscountValue());
        coupon.setMinOrderValue(updateCouponRequestDto.getMinOrderValue());
        coupon.setIsActive(updateCouponRequestDto.getIsActive());

       Coupon savedCoupon=repository.save(coupon);
       UpdateCouponResponseDto updateCouponResponseDto=modelMapper.map(savedCoupon,UpdateCouponResponseDto.class);
        return updateCouponResponseDto;
    }
    @PreAuthorize("hasRole('RESTAURANT_MANAGER')")
    @Override
    public List<Coupon> getAllByRestaurantId() {
        //from JWT later
        UUID restaurantId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return repository.findAllByRestaurantId(restaurantId);
    }
    //for access by anyone if anyone needs list of coupons
    //NOT YET WORKING in security sense jwt shouldn't block but its blocking
    @Override
    public List<Coupon> getAllByRestaurantId(UUID restaurantId) {
        return repository.findAllByRestaurantId(restaurantId);
    }

    @PreAuthorize("hasRole('RESTAURANT_MANAGER')")
    @Override
    public UpdateCouponResponseDto updateToExpiredByCouponId(UUID id) {
        //restaurantId from jwt
        UUID restaurantId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Coupon coupon=repository.findById(id).get();
        if(!repository.findById(id).isPresent())
            throw new RuntimeException("No such coupon found");

        if(!coupon.getRestaurantId().equals(restaurantId))
              throw new RuntimeException("This coupon doesn't belongs to you, check id properly");

          coupon.setIsActive(false);
          Coupon savedCoupon=repository.save(coupon);
          return modelMapper.map(savedCoupon,UpdateCouponResponseDto.class);

    }

    @PreAuthorize("hasRole('RESTAURANT_MANAGER')")
    @Override
    public String deleteByCouponId(UUID id) {
        //restaurantId from jwt
        UUID restaurantId= ((CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Coupon coupon=repository.findById(id).get();
        if(!repository.findById(id).isPresent())
            throw new RuntimeException("Cannot delete,No such coupon found");

        if(!coupon.getRestaurantId().equals(restaurantId))
            throw new RuntimeException("you do not have access to delete coupon because it doesn't belongs to you, check id properly");

       repository.deleteById(id);
        return "Coupon with Id: "+id+" deleted";
    }


}

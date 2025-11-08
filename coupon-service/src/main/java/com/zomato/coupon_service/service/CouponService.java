package com.zomato.coupon_service.service;

import com.zomato.coupon_service.dto.addCoupon.AddCouponRequestDto;
import com.zomato.coupon_service.dto.addCoupon.AddCouponResponseDto;
import com.zomato.coupon_service.dto.updateCoupon.UpdateCouponRequestDto;
import com.zomato.coupon_service.dto.updateCoupon.UpdateCouponResponseDto;
import com.zomato.coupon_service.entity.Coupon;
import com.zomato.coupon_service.repository.CouponRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CouponService implements CouponServiceInterface{
    @Autowired
    private CouponRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddCouponResponseDto save(AddCouponRequestDto addCouponRequestDto) {
        //later get restaurantId from JWT
        UUID restaurantId= UUID.fromString("2984c037-b75c-49a8-973f-2ebd88a26a15");

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

    @Override
    public UpdateCouponResponseDto update(UpdateCouponRequestDto updateCouponRequestDto) {
        //later get restaurantId from JWT
        UUID restaurantId= UUID.fromString("2984c037-b75c-49a8-973f-2ebd88a26a15");

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

    @Override
    public List<Coupon> getAllByRestaurantId() {
        //get it from JWT later
        UUID restaurantId= UUID.fromString("2984c037-b75c-49a8-973f-2ebd88a26a15");
        return repository.findAllByRestaurantId(restaurantId);
    }

    @Override
    public UpdateCouponResponseDto updateToExpiredByCouponId(UUID id) {
        //get restaurantId from jwt
        UUID restaurantId=UUID.fromString("2984c037-b75c-49a8-973f-2ebd88a26a15");

        Coupon coupon=repository.findById(id).get();
        if(!repository.findById(id).isPresent())
            throw new RuntimeException("No such coupon found");

        if(!coupon.getRestaurantId().equals(restaurantId))
              throw new RuntimeException("This coupon doesn't belongs to you, check id properly");

          coupon.setIsActive(false);
          Coupon savedCoupon=repository.save(coupon);
          return modelMapper.map(savedCoupon,UpdateCouponResponseDto.class);

    }

    @Override
    public String deleteByCouponId(UUID id) {
        //get restaurantId from jwt
        UUID restaurantId=UUID.fromString("2984c037-b75c-49a8-973f-2ebd88a26a15");

        Coupon coupon=repository.findById(id).get();
        if(!repository.findById(id).isPresent())
            throw new RuntimeException("Cannot delete,No such coupon found");

        if(!coupon.getRestaurantId().equals(restaurantId))
            throw new RuntimeException("you do not have access to delete coupon because it doesn't belongs to you, check id properly");

       repository.deleteById(id);
        return "Coupon with Id: "+id+" deleted";
    }
}

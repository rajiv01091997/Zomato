package com.zomato.coupon_service.repository;

import com.zomato.coupon_service.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {


   Optional<Coupon> findByCouponCodeAndRestaurantId(String couponCode, UUID restaurantId);

   Optional<Coupon> findByCouponCodeAndRestaurantIdAndIsActive(String couponCode, UUID restaurantId,
                                                               boolean isActive );
   List<Coupon> findAllByRestaurantId(UUID restaurantId);
}

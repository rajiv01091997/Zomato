package com.zomato.cart_service.repository;

import com.zomato.cart_service.entity.Cart;
import com.zomato.cart_service.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

//    @Query("SELECT c FROM Cart c WHERE c.customerId = :customerId AND c.restaurantId = :restaurantId AND c.status = 'ACTIVE'")
//    Optional<Cart> findActiveCart(UUID customerId, UUID restaurantId);

    Optional<Cart> findCartByCustomerIdAndRestaurantIdAndStatus(UUID customerId,
                                                                UUID restaurantId,
                                                                CartStatus status);
}

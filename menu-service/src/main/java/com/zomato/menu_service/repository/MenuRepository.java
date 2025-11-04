package com.zomato.menu_service.repository;

import com.zomato.menu_service.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {
    Optional<Menu> findByItemNameAndRestaurantId(String itemName,UUID restaurantId);
}

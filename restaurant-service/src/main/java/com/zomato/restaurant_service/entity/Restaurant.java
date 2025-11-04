package com.zomato.restaurant_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name="restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private String name;
    private String description;
    private String isPureVeg;
    private String Address;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

  @OneToOne(mappedBy = "restaurant",cascade= CascadeType.ALL,orphanRemoval = true)
  private RestaurantBankDetail bankDetail;
}

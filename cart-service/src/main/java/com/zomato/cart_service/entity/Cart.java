package com.zomato.cart_service.entity;

import com.zomato.cart_service.enums.CartStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Cart {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID cartId;
    private UUID customerId;
    @Column(nullable = false)
    private UUID restaurantId;
    @Enumerated(EnumType.STRING)
    private CartStatus status;
    private double grossAmount;
    private double totalAmount;
    @Column(nullable = true)
    private String couponCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> itemList;

}

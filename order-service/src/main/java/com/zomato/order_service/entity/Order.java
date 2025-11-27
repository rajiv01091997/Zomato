package com.zomato.order_service.entity;

import com.zomato.order_service.enums.OrderStatus;
import com.zomato.order_service.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity
public class Order {
    @Id
    private String id;
    private UUID cartId;
    private UUID customerId;
    private UUID restaurantId;
    private UUID riderId;
    private String invoiceNumber;
    private String couponCode;
    private OrderStatus orderStatus;//updated by system first as PLACED if success payment else FAILED thn and there
    //failed should not be updated any further
    //else rider/restaurant can update their corresponding columns
    private PaymentStatus paymentStatus;//should come from payment service
    private LocalDateTime orderTime;//jpa will populate
    private LocalDateTime updateTime;
    private LocalDateTime deliveryTime;//rider will update on delivery
    private double totalAmount;//if cart price matches current prices then just apply coupon if applicable
    private String deliveryAddress; //fetch from customerId from user-service
    private String specialInstructions;

    @PrePersist
    public void prePersist() {
        this.orderTime = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    @Version
    private Long version;//for preventing concurrent modification

}

package com.zomato.order_service.entity;

import com.zomato.order_service.dto.kafka.payment.PaymentStatus;
import com.zomato.order_service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
@Entity(name="orders")
public class Order {
    @Id
    private String id;
    private UUID cartId;
    private UUID customerId;//fetch from jwt and then from cart and proceed if matches
    private UUID restaurantId;//from cart
    private UUID riderId;//rider can update when he picks
    private String invoiceNumber;
    private String couponCode;//from cart
    private OrderStatus orderStatus;//updated by system first as PLACED if success payment else FAILED thn and there
    //failed should not be updated any further
    //else rider/restaurant can update their corresponding columns
    private String paymentId;
    private PaymentStatus paymentStatus;//should be pending first then update comes from payment service
    private LocalDateTime orderTime;//jpa will populate
    private LocalDateTime updateTime;
    private LocalDateTime deliveryTime;//rider will update on delivery
    private double totalAmount;//if cart price matches current prices then just apply coupon if applicable
    private  double grossAmount;
    private double deliveryCharge;
    private double platformFee;
    private double gstAmount;
    private double couponDiscount;
    private String deliveryAddress; //fetch from customerId from user-service
    private String specialInstructions;
    private String otp;

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

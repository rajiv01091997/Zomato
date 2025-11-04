package com.zomato.user_service.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name="CustomerAddress")
public class CustomerAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
   private UUID id;
   private String addressLine;
   private Double latitude;
   private Double longitude;
   private Boolean isDefault;
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


 @ManyToOne
 @JoinColumn(name="user_id",nullable = false)
 private Users users;
}

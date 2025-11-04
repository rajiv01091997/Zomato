package com.zomato.user_service.entity;

import com.zomato.user_service.enums.Role;
import com.zomato.user_service.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true,nullable = false)
    private String userName;
    private String password;
    @Column(unique = true,nullable = false)
    private String email;
    @Column(unique = true,nullable = false)
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Status status;
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

    @OneToMany(mappedBy = "users",cascade= CascadeType.ALL, orphanRemoval = true)
    private List<CustomerAddress> customerAddressList =new ArrayList<>();

    @OneToOne(mappedBy = "users",cascade = CascadeType.ALL, orphanRemoval = true)
    private RiderDetails riderDetails;

    @OneToOne(mappedBy = "users",cascade = CascadeType.ALL, orphanRemoval = true)
    private RestaurantDetails restaurantDetails;


}

package com.zomato.cart_service.security;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class CustomPrincipal {
    private UUID id;
    private String userName;
    private String phoneNumber;
    private String email;
    private String status;
}

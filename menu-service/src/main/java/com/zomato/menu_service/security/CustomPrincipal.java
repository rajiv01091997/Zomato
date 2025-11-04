package com.zomato.menu_service.security;

import com.zomato.user_service.enums.Status;
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

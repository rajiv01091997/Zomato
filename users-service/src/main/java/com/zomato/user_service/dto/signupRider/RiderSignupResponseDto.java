package com.zomato.user_service.dto.signupRider;

import com.zomato.user_service.enums.Role;
import com.zomato.user_service.enums.Status;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RiderSignupResponseDto {
    private UUID id;
    private String userName;
    private String email;
    private String phoneNumber;
    private Role role;
    private Status status;

    private RiderDetailsResponseDto riderDetailsResponseDto;
}

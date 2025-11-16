package com.zomato.user_service.dto.mail;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class SignUpMailDto {
    private String userName;
    private String email;
    private String restaurantName;//only for restaurants
    private LocalDateTime creationTime;
}

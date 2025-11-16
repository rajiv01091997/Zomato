package com.zomato.mail_service.dto;

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
    private String restaurantName;
    private LocalDateTime creationTime;
}

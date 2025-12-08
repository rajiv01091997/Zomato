package com.zomato.mail_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MailDto {
    private String userName;
    private String email;
    private String restaurantName;
    private String orderId;
    private byte[] attachment;
    private String attachmentName;
    private LocalDateTime creationTime;
    private String extraInfo;
}

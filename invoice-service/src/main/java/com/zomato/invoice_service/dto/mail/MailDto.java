package com.zomato.invoice_service.dto.mail;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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

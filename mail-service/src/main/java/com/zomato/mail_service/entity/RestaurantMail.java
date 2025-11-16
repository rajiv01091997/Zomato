package com.zomato.mail_service.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "restaurant_mails")
public class RestaurantMail {
    @Id
    private String id;
    private String userName;
    private String to;
    private String subject;
    private String body;
    private byte[] attachment;
    private String attachmentName;
    @CreatedDate
    private LocalDateTime createdAt;
}

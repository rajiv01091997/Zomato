package com.zomato.menu_service.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
public class ExceptionResponse {
private String message;
private HttpStatus status;
private String path;
private LocalDateTime timestamp;
}

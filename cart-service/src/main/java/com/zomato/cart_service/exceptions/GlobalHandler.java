package com.zomato.cart_service.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalHandler {
    @ExceptionHandler(ActiveCartDuplicacyException.class)
    public ExceptionResponse handleActiveCartDuplicacyException(ActiveCartDuplicacyException ex, HttpServletRequest request)
    {
      return ExceptionResponse.builder()
              .timestamp(LocalDateTime.now())
              .path(request.getRequestURI())
              .message(ex.getExceptionMsg())
              .status(HttpStatus.NOT_ACCEPTABLE)
              .build();
    }
    @ExceptionHandler(CartContainsMultipleRestaurantsException.class)
    public ExceptionResponse handleMultipleRestaurantInSingleCartException(CartContainsMultipleRestaurantsException ex, HttpServletRequest request)
    {
        return ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .message(ex.getExceptionMsg())
                .status(HttpStatus.NOT_ACCEPTABLE)
                .build();
    }
    @ExceptionHandler(ItemsNotAvailableException.class)
    public ExceptionResponse handleItemsNotAvailableException(ItemsNotAvailableException ex, HttpServletRequest request)
    {
        return ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .message(ex.getExceptionMsg())
                .status(HttpStatus.NOT_ACCEPTABLE)
                .build();
    }
    @ExceptionHandler(Exception.class)
    public ExceptionResponse handleException(Exception ex, HttpServletRequest request)
    {
        return ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .status(HttpStatus.NOT_ACCEPTABLE)
                .build();
    }

}

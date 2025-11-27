package com.zomato.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
    public class GlobalHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ExceptionResponse handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

            Map<String, String> fieldErrors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error -> {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            });

            // Convert map to a readable string or store map in message field as JSON
            String message = fieldErrors.toString();

            return ExceptionResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .path(request.getRequestURI())
                    .message(message)
                    .status(HttpStatus.BAD_REQUEST)
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


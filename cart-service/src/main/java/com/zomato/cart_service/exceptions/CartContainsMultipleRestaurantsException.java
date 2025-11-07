package com.zomato.cart_service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartContainsMultipleRestaurantsException extends RuntimeException{
  private String exceptionMsg;
}

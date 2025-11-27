package com.zomato.menu_service.dto.add;

import com.zomato.menu_service.enums.Course;
import com.zomato.menu_service.enums.Kind;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class AddToMenuResponseDto {
    private UUID itemId;
    private String itemName;
    private double price;
    private Course course;
    private Kind kind;
    private boolean available;
    private UUID restaurantId;
}

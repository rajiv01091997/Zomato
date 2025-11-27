package com.zomato.menu_service.dto.add;

import com.zomato.menu_service.enums.Course;
import com.zomato.menu_service.enums.Kind;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class AddToMenuRequestDto {
    private String itemName;
    private double price;
    private Course course;
    private Kind kind;
    private boolean available;
}

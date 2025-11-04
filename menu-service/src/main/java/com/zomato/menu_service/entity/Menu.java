package com.zomato.menu_service.entity;

import com.zomato.menu_service.enums.Course;
import com.zomato.menu_service.enums.Kind;
import com.zomato.user_service.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity(name="Menu")
public class Menu {
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "id")
    @Id
    private UUID itemId;
    private String itemName;
    private int price;
    private Course course;
    private Kind kind;
    private boolean available;

    private UUID restaurantId;

}

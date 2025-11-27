package com.zomato.order_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Sequence {
    @Id
    private Long id;
    private Long counter;

}

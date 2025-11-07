package com.zomato.cart_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@IdClass(ItemId.class)
@Entity
public class Item {
    @Id
    @Column(nullable = false)
    private UUID itemId;
    @Id
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;
    private int quantity;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
class ItemId implements Serializable {
    private UUID cart;
    private UUID itemId;


}

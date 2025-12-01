//package com.zomato.user_service.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.UUID;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//@Entity
//public class Admin {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//    //private AdminLevel adminLevel;
//
//    @OneToOne
//    @JoinColumn(name="user_id",nullable=false)
//    private Users users;
//}

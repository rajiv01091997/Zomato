package com.zomato.mail_service.repository;

import com.zomato.mail_service.entity.RestaurantMail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends MongoRepository<RestaurantMail,String> {
}

package com.zomato.mail_service.repository;

import com.zomato.mail_service.entity.Mail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailRepository extends MongoRepository<Mail,String> {
    Optional<Mail> findByOrderId(String orderId);
}

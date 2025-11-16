package com.zomato.user_service.configurations;

import com.zomato.user_service.enums.Role;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class Config {

    @Bean
    public ModelMapper getMapper()
    {
        return new ModelMapper();
    }

}

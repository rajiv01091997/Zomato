package com.zomato.menu_service.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class configs {

    @Bean
    public ModelMapper getModelMapper()
    {
        return new ModelMapper();
    }
}

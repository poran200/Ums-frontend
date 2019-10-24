package com.seu.ums.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication

public class FrontendApplication {
    @Bean
    RestTemplate restTemplate (){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(FrontendApplication.class, args);
    }

}

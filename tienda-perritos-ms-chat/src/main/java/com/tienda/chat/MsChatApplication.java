package com.tienda.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class MsChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsChatApplication.class, args);
    }
}

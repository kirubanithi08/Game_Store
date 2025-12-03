package com.example.GameStore.Entity;

import com.example.GameStore.Dto.AuthRequest;
import com.example.GameStore.Service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Admin {

    @Bean
    CommandLineRunner createDefaultAdmin(AuthService authService) {
        return args -> {
            if (!authService.userExists("goku")) {
                authService.registerAdmin(new AuthRequest("goku", "123"));
                System.out.println("Default admin created");
            }
        };
    }
}


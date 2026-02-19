package com.example.GameStore.modules.identity.config;

import com.example.GameStore.modules.identity.dto.AuthRequest;
import com.example.GameStore.modules.identity.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AdminConfig {

    @Bean
    CommandLineRunner createDefaultAdmin(AuthService authService) {
        return args -> {
            if (!authService.userExists("goku")) {
                authService.registerAdmin(new AuthRequest("goku", "123"));
                log.info("Default admin created");
            }
        };
    }
}

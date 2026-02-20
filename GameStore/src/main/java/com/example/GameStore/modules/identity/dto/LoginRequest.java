package com.example.GameStore.modules.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record LoginRequest (

        @NotBlank(message = "Username is required")
        @Email
                String email,

        @NotBlank(message = "Password is required")
        String password
){}


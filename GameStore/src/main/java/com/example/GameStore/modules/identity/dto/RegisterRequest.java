package com.example.GameStore.modules.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "Username is required")
    String username,

    @NotBlank(message = "Username is required")
    @Email
    String email,
    
    @NotBlank(message = "Password is required")
    String password
) {}

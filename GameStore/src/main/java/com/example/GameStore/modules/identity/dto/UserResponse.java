package com.example.GameStore.modules.identity.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {
}

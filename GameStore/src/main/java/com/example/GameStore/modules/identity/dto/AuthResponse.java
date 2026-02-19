package com.example.GameStore.modules.identity.dto;

public record AuthResponse(
    String accessToken,
    String username,
    String role
) {}

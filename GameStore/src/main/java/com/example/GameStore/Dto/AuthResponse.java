package com.example.GameStore.Dto;

import lombok.Data;

@Data
public class AuthResponse {
    String AccessToken;
    String RefreshToken;
}

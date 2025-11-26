package com.example.GameStore.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String AccessToken;
    private String username;
    private String role;

}

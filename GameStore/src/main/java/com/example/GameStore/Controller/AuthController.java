package com.example.GameStore.Controller;


import com.example.GameStore.Dto.AuthRequest;
import com.example.GameStore.Dto.AuthResponse;
import com.example.GameStore.Service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest authRequest) {


        authService.userRegister(authRequest);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    ResponseEntity<AuthResponse>login(@RequestBody AuthRequest authRequest){
       AuthResponse response= authService.userLogin(authRequest);
        return ResponseEntity.ok(response);
    }
}

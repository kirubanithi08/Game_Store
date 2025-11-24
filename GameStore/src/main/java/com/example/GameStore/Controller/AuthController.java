package com.example.GameStore.Controller;

import com.example.GameStore.Dto.AuthRequest;
import com.example.GameStore.Dto.AuthResponse;
import com.example.GameStore.Security.JwtUtils;
import com.example.GameStore.Service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest authRequest) {

        authService.userRegister(authRequest);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {

        AuthResponse response = authService.userLogin(authRequest);

        String refreshToken = authService.generateRefreshToken(authRequest.getUsername());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .ok()
                .header("Set-Cookie", cookie.toString())
                .body(response);
    }



    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        String username = jwtUtils.extractUsername(refreshToken);

        jwtUtils.validateRefreshToken(refreshToken);

        String newAccessToken = jwtUtils.generateToken(username);

        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }


    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
//                .path("/api/auth/refresh")
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .ok()
                .header("Set-Cookie", deleteCookie.toString())
                .body(Map.of("message", "Logged out successfully"));
    }


}

package com.example.GameStore.modules.identity.controller;

import com.example.GameStore.modules.identity.dto.AuthRequest;
import com.example.GameStore.modules.identity.dto.AuthResponse;
import com.example.GameStore.modules.identity.entity.User;
import com.example.GameStore.modules.identity.security.JwtUtils;
import com.example.GameStore.modules.identity.service.AuthService;
import com.example.GameStore.modules.shared.dto.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody AuthRequest authRequest) {
        authService.userRegister(authRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.userLogin(authRequest);

        String refreshToken = jwtUtils.generateRefreshToken(response.username());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // Set to true in production
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(ApiResponse.success(response, "Login successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, String>>> getMe(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(ApiResponse.error("Missing or invalid Authorization header"));
        }

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtils.extractUsername(token);
        User user = authService.getUser(username);

        return ResponseEntity.ok(ApiResponse.success(
                Map.of("username", user.getUsername(), "role", user.getRole())
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Missing refresh token"));
        }

        try {
            if (!jwtUtils.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Invalid refresh token"));
            }

            String username = jwtUtils.extractUsername(refreshToken);
            User user = authService.getUser(username);

            String newAccessToken = jwtUtils.generateToken(user.getUsername(), user.getRole());

            return ResponseEntity.ok(ApiResponse.success(
                    new AuthResponse(newAccessToken, user.getUsername(), user.getRole())
            ));
        } catch (JwtException | IllegalArgumentException e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Token refresh failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", deleteCookie.toString())
                .body(ApiResponse.success("Logged out successfully"));
    }
}

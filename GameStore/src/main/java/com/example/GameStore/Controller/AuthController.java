package com.example.GameStore.Controller;

import com.example.GameStore.Dto.AuthRequest;
import com.example.GameStore.Dto.AuthResponse;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Security.JwtUtils;
import com.example.GameStore.Service.AuthService;
import io.jsonwebtoken.JwtException;
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

        String refreshToken = jwtUtils.generateRefreshToken(
                response.getUsername()
        );

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // set to true in production (HTTPS)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity
                .ok()
                .header("Set-Cookie", cookie.toString())
                .body(response);
    }


    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
        }

        String token = authHeader.replace("Bearer ", "");

        String username = jwtUtils.extractUsername(token);

        User user = authService.getUser(username);

        return ResponseEntity.ok(
                Map.of(
                        "username", user.getUsername(),
                        "role", user.getRole()
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        try {

            if (!jwtUtils.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(401).build();
            }

            String username = jwtUtils.extractUsername(refreshToken);

            User user = authService.getUser(username);

            String newAccessToken = jwtUtils.generateToken(
                    username,
                    user.getRole()
            );

            return ResponseEntity.ok(
                    new AuthResponse(
                            newAccessToken,
                            user.getUsername(),
                            user.getRole()
                    )
            );
        } catch (JwtException | IllegalArgumentException e) {

            return ResponseEntity.status(401).build();
        }
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

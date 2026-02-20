package com.example.GameStore.modules.identity.service;

import com.example.GameStore.modules.identity.dto.AuthResponse;
import com.example.GameStore.modules.identity.dto.LoginRequest;
import com.example.GameStore.modules.identity.dto.RegisterRequest;
import com.example.GameStore.modules.identity.entity.User;
import com.example.GameStore.modules.identity.repository.UserRepository;
import com.example.GameStore.modules.identity.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public User userRegister(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = User.builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role("User")
                .build();

        return userRepository.save(user);
    }

    public AuthResponse userLogin(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        User user = userRepository.findByUsername(loginRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtUtils.generateToken(user.getUsername(), user.getRole());

        return new AuthResponse(accessToken, user.getUsername(), user.getRole());
    }

    public User registerAdmin(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new IllegalArgumentException("Admin username already taken");
        }

        User admin = User.builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role("Admin")
                .build();

        return userRepository.save(admin);
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public String generateRefreshToken(String username) {
        return jwtUtils.generateRefreshToken(username);
    }
}

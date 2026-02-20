package com.example.GameStore.modules.identity.service;

import com.example.GameStore.modules.identity.dto.AuthRequest;
import com.example.GameStore.modules.identity.dto.AuthResponse;
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

    public User userRegister(AuthRequest authRequest) {
        if (userRepository.findByUsername(authRequest.username()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = User.builder()
                .username(authRequest.username())
                .password(passwordEncoder.encode(authRequest.password()))
                .role("User")
                .build();

        return userRepository.save(user);
    }

    public AuthResponse userLogin(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );

        User user = userRepository.findByUsername(authRequest.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtUtils.generateToken(user.getUsername(), user.getRole());

        return new AuthResponse(accessToken, user.getUsername(), user.getRole());
    }

    public User registerAdmin(AuthRequest authRequest) {
        if (userRepository.findByUsername(authRequest.username()).isPresent()) {
            throw new IllegalArgumentException("Admin username already taken");
        }

        User admin = User.builder()
                .username(authRequest.username())
                .password(passwordEncoder.encode(authRequest.password()))
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

package com.example.GameStore.Service;

import com.example.GameStore.Dto.AuthRequest;
import com.example.GameStore.Dto.AuthResponse;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.UserRepository;
import com.example.GameStore.Security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
    AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.jwtUtils=jwtUtils;
    }

    public User userRegister(AuthRequest authRequest) {

        if (userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        return userRepository.save(user);
    }


public AuthResponse userLogin(AuthRequest authRequest){
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()

            )
    );

    String token=jwtUtils.generateToken(authRequest.getUsername());

    return new AuthResponse(token);
}
}

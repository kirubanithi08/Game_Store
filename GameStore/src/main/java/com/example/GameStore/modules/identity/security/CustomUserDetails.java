package com.example.GameStore.modules.identity.security;

import com.example.GameStore.modules.identity.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = Optional.ofNullable(user.getRole())
                .map(String::trim)
                .map(String::toLowerCase)
                .map(r -> switch (r) {
                    case "admin" -> "ROLE_ADMIN";
                    case "user" -> "ROLE_USER";
                    default -> r.startsWith("role_") ? r.toUpperCase() : "ROLE_" + r.toUpperCase();
                })
                .orElse("ROLE_USER");

        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
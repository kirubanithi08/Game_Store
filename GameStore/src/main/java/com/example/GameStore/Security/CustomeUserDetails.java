package com.example.GameStore.Security;

import com.example.GameStore.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomeUserDetails implements UserDetails {


    private final User user;

    public CustomeUserDetails(User user){
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getUsername();
    }

    @Override
    public String getUsername() {
        return user.getPassword();
    }
}

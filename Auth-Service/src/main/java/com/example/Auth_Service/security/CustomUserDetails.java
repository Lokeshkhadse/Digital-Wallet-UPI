package com.example.Auth_Service.security;


import com.example.Auth_Service.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CustomUserDetails
        implements UserDetails {

    private User user;

    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {

        return Arrays.stream(user.getRoles().split(","))
                .map(role ->
                        new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {

        return user.getPassword();
    }

    @Override
    public String getUsername() {

        // LOGIN USING EMAIL
        return user.getEmail();
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
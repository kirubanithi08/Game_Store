package com.example.GameStore.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("[DEBUG] Incoming request path: " + path);

        // Skip public endpoints
        if (path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/api/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        System.out.println("[DEBUG] Authorization header: " + authHeader);

        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("[DEBUG] Extracted token: " + token);

            try {
                username = jwtUtils.extractUsername(token);
                System.out.println("[DEBUG] Username from token: " + username);
            } catch (Exception e) {
                System.out.println("[DEBUG] Failed to extract username: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("[DEBUG] Loaded user details: " + userDetails.getUsername());

            if (jwtUtils.validateToken(token, userDetails.getUsername())) {
                String role = jwtUtils.extractRole(token);
                System.out.println("[DEBUG] Role from token before normalization: " + role);

                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                System.out.println("[DEBUG] Role after normalization: " + role);

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(authority)
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("[DEBUG] SecurityContext authentication set: " + SecurityContextHolder.getContext().getAuthentication());
            } else {
                System.out.println("[DEBUG] Token validation failed");
            }
        } else if (username == null) {
            System.out.println("[DEBUG] Username is null or authentication already exists");
        }

        filterChain.doFilter(request, response);
    }
}

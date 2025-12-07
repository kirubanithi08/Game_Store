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
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Print detailed debug info
        System.out.println("[DEBUG][SkipCheck] method=" + method +
                " | servletPath=" + path +
                " | requestURI=" + uri +
                " | query=" + request.getQueryString());

        // Public auth endpoints
        if (path.matches("/api/auth/(login|register|refresh)")) {
            System.out.println("[DEBUG][SkipCheck] Skipping auth endpoints");
            return true;
        }

        // Skip OPTIONS preflight
        if (method.equals("OPTIONS")) {
            System.out.println("[DEBUG][SkipCheck] Skipping OPTIONS preflight");
            return true;
        }

        // Skip all public GET requests for games or genres
        if (method.equals("GET") && (path.contains("games") || path.contains("genres"))) {
            System.out.println("[DEBUG][SkipCheck] Skipping public GET for games/genres");
            return true;
        }

        // All others → filter will run
        System.out.println("[DEBUG][SkipCheck] Running JWT filter for this request");
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                System.out.println("[DEBUG] Token extraction failed: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(token, userDetails.getUsername())) {
                String role = jwtUtils.extractRole(token);
                if (!role.startsWith("ROLE_")) role = "ROLE_" + role;

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("[DEBUG] JWT validated, authentication set for: " + username);
            }
        } else {
            if (username == null) {
                System.out.println("[DEBUG] No username extracted from token, skipping auth");
            } else {
                System.out.println("[DEBUG] Authentication already exists in context, skipping");
            }
        }

        filterChain.doFilter(request, response);
    }
}

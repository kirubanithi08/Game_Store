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

        String servletPath = request.getServletPath();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Full debug logging
        System.out.println("[DEBUG][SkipCheck] method=" + method
                + " | servletPath=" + servletPath
                + " | requestURI=" + uri
                + " | contextPath=" + request.getContextPath()
                + " | pathInfo=" + request.getPathInfo()
                + " | query=" + request.getQueryString()
        );

        // 1️⃣ Public auth endpoints
        if (uri.matches("/api/auth/(login|register|refresh)")) {
            System.out.println("[DEBUG] Skipping auth endpoints");
            return true;
        }

        // 2️⃣ Always skip OPTIONS preflight
        if (method.equals("OPTIONS")) {
            System.out.println("[DEBUG] Skipping OPTIONS preflight");
            return true;
        }

        // 3️⃣ Public GET requests for games + genres
        if (method.equals("GET")) {

            if (uri.contains("/api/games")) {
                System.out.println("[DEBUG] Skipping public GET for games: " + uri);
                return true;
            }

            if (uri.contains("/api/genres")) {
                System.out.println("[DEBUG] Skipping public GET for genres: " + uri);
                return true;
            }
        }

        // 4️⃣ Everything else must be filtered
        System.out.println("[DEBUG] Running JWT filter for this request");
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        final String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // Extract JWT token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                System.out.println("[DEBUG] Token extraction failed: " + e.getMessage());
            }
        }

        // Validate token + set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(token, userDetails.getUsername())) {

                String role = jwtUtils.extractRole(token);
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("[DEBUG] JWT validated, authenticated: " + username);
            }
        }

        filterChain.doFilter(request, response);
    }
}

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
import java.util.Locale;

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

        String method = request.getMethod() == null ? "" : request.getMethod().toUpperCase(Locale.ROOT);

        String path = request.getRequestURI();
        if (path == null) path = "";
        path = path.trim();

        System.out.println("[DEBUG][SkipCheck] method=" + method + " | path=" + path);

        // 1) Root + health checks
        if (path.equals("/") || path.equals("/health") || path.equals("/healthz")) {
            return true;
        }

        // 2) Static files
        if (path.matches(".*\\.(css|js|png|jpg|jpeg|webp|svg)$")) {
            return true;
        }

        // 3) Public auth endpoints only
        if (path.equals("/api/auth/login") ||
                path.equals("/api/auth/register") ||
                path.equals("/api/auth/refresh")) {
            return true;
        }

        // ❗ /api/auth/me should NOT be skipped — it requires JWT
        // Let filter process it.

        // 4) Preflight
        if (method.equals("OPTIONS") || method.equals("HEAD")) {
            return true;
        }

        // 5) Public GET for games & genres
        if (method.equals("GET")) {
            if (path.startsWith("/api/games/") && !path.matches(".*/(edit|admin)$")) {
                return true;
            }
            if (path.startsWith("/api/genres")) {
                return true;
            }
        }

        // Protected routes → must process JWT
        System.out.println("[DEBUG] Running JWT filter for: " + path);
        return false;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        try {
            final String authHeader = request.getHeader("Authorization");

            // If header is missing or not Bearer, just continue (public or unauthenticated request)
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7).trim();
            if (token.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = null;
            try {
                username = jwtUtils.extractUsername(token);
            } catch (Exception ex) {
                // invalid token or extraction failed — log and continue without authenticating
                System.out.println("[DEBUG] Token extraction failed: " + ex.getMessage());
                filterChain.doFilter(request, response);
                return;
            }

            // Only authenticate if we have a username and no existing authentication
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails;
                try {
                    userDetails = userDetailsService.loadUserByUsername(username);
                } catch (Exception ex) {
                    // user not found or other issue — log and continue
                    System.out.println("[DEBUG] UserDetails load failed for " + username + " : " + ex.getMessage());
                    filterChain.doFilter(request, response);
                    return;
                }

                boolean valid = false;
                try {
                    valid = jwtUtils.validateToken(token, userDetails.getUsername());
                } catch (Exception ex) {
                    System.out.println("[DEBUG] Token validation threw: " + ex.getMessage());
                    // don't authenticate; continue
                }

                if (valid) {
                    String role = null;
                    try {
                        role = jwtUtils.extractRole(token);
                    } catch (Exception ex) {
                        System.out.println("[DEBUG] Role extraction failed: " + ex.getMessage());
                    }

                    if (role == null) role = "USER";
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

                    System.out.println("[DEBUG] JWT validated → authenticated " + username);
                }
            }

            // Continue the chain in normal flow
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Defensive catch-all: log and continue so filter never causes 500
            System.out.println("[ERROR] Unexpected error in JwtAuthenticationFilter: " + e.getMessage());
            e.printStackTrace();
            filterChain.doFilter(request, response);
        }
    }
}

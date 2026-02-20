package com.example.GameStore.modules.identity.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod() == null ? "" : request.getMethod().toUpperCase(Locale.ROOT);
        String path = request.getRequestURI();
        if (path == null) path = "";

        if (path.equals("/") || path.equals("/health") || path.equals("/healthz")) {
            return true;
        }

        if (path.matches(".*\\.(css|js|png|jpg|jpeg|webp|svg)$")) {
            return true;
        }

        if (path.equals("/api/auth/login") ||
                path.equals("/api/auth/register") ||
                path.equals("/api/auth/refresh")) {
            return true;
        }

        if (method.equals("OPTIONS") || method.equals("HEAD")) {
            return true;
        }

        if (method.equals("GET")) {
            if (path.startsWith("/api/games/") && !path.matches(".*/(edit|admin)$")) {
                return true;
            }
            if (path.startsWith("/api/genres")) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7).trim();
            if (token.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtils.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtils.validateToken(token, userDetails.getUsername())) {
                    String role = jwtUtils.extractRole(token);
                    if (role == null) role = "USER";
                    
                    String mappedRole = role;
                    if (role.equalsIgnoreCase("Admin")) {
                        mappedRole = "ROLE_ADMIN";
                    } else if (role.equalsIgnoreCase("User")) {
                        mappedRole = "ROLE_USER";
                    } else if (!role.startsWith("ROLE_")) {
                        mappedRole = "ROLE_" + role.toUpperCase();
                    }
                    
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    List.of(new SimpleGrantedAuthority(mappedRole))
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error in JwtAuthenticationFilter: ", e);
            filterChain.doFilter(request, response);
        }
    }
}

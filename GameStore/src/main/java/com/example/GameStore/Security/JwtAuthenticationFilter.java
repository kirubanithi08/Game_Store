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

    /**
     * Decide which requests should NOT be filtered by this JWT filter.
     * This method is defensive: it normalizes the request path (removes contextPath),
     * handles nulls safely, and skips common public endpoints + health checks + static files.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String method = request.getMethod() == null ? "" : request.getMethod().toUpperCase(Locale.ROOT);

        String requestUri = request.getRequestURI() == null ? "" : request.getRequestURI();
        String contextPath = request.getContextPath() == null ? "" : request.getContextPath();

        // Safely derive the path relative to contextPath
        String path = requestUri;
        if (!contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
            path = requestUri.substring(contextPath.length());
        }

        if (path == null) path = "";
        path = path.trim();

        // For debug (you can remove or lower log verbosity later)
        System.out.println("[DEBUG][SkipCheck] method=" + method + " | path=" + path);

        // 1) Root + health checks (Render calls HEAD / and GET /)
        if (path.equals("/") || path.isEmpty() || path.equalsIgnoreCase("/health") || path.equalsIgnoreCase("/healthz")) {
            System.out.println("[DEBUG] Skipping root/health path: " + path);
            return true;
        }

        // 2) Static assets often requested by browsers
        if (path.equalsIgnoreCase("/favicon.ico")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".webp")
                || path.endsWith(".svg")) {
            System.out.println("[DEBUG] Skipping static asset: " + path);
            return true;
        }

        // 3) Auth endpoints (public)
        // matches any path under /api/auth/ like /api/auth/login, /api/auth/register, /api/auth/refresh
        if (path.startsWith("/api/auth/")) {
            System.out.println("[DEBUG] Skipping auth endpoints: " + path);
            return true;
        }

        // 4) Preflight & HEAD should be skipped
        if ("OPTIONS".equals(method) || "HEAD".equals(method)) {
            System.out.println("[DEBUG] Skipping " + method + " request for: " + path);
            return true;
        }

        // 5) Public GET APIs for games and genres
        if ("GET".equals(method)) {
            if (path.startsWith("/api/games")) {
                System.out.println("[DEBUG] Skipping public GET /api/games*: " + path);
                return true;
            }
            if (path.startsWith("/api/genres")) {
                System.out.println("[DEBUG] Skipping public GET /api/genres*: " + path);
                return true;
            }
        }

        // Otherwise the filter should run (protected route)
        System.out.println("[DEBUG] Running JWT filter for this request");
        return false;
    }

    /**
     * Main filter logic. This implementation is defensive:
     * - it never throws an exception to the servlet container (catches and logs),
     * - it always calls filterChain.doFilter so requests keep flowing,
     * - it validates token only when present and well-formed.
     */
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

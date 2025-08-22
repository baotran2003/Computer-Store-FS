package com.example.ComputerStore.config;

import com.example.ComputerStore.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Skip filter for public endpoints
        String requestPath = request.getRequestURI();
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Validate token
                if (jwtService.isTokenValid(jwt, userEmail)) {
                    
                    // Extract role from token
                    String role = jwtService.extractClaim(jwt, claims -> claims.get("role", String.class));
                    
                    // Create authorities
                    List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + (role != null ? role.toUpperCase() : "USER"))
                    );

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            authorities
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("JWT authentication successful for user: {} with role: {}", userEmail, role);
                }
            }
        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestPath) {
        return requestPath.startsWith("/api/auth/register") ||
               requestPath.startsWith("/api/auth/login") ||
               requestPath.startsWith("/api/auth/google") ||
               requestPath.startsWith("/api/auth/forgot-password") ||
               requestPath.startsWith("/api/auth/reset-password") ||
               requestPath.startsWith("/api/auth/refresh-token") ||
               requestPath.startsWith("/api/products") ||
               requestPath.startsWith("/api/categories") ||
               requestPath.startsWith("/api/blogs") ||
               requestPath.startsWith("/v3/api-docs") ||
               requestPath.startsWith("/swagger-ui");
    }
}

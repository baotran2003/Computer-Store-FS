package com.example.ComputerStore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Disable form login and basic auth
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - KHÔNG CẦN xác thực
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/google").permitAll()
                        .requestMatchers("/api/auth/forgot-password").permitAll()
                        .requestMatchers("/api/auth/reset-password").permitAll()
                        .requestMatchers("/api/auth/refresh-token").permitAll()
                        
                        // Public product endpoints
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/api/categories/**").permitAll()
                        .requestMatchers("/api/blogs/**").permitAll()
                        
                        // Swagger/Documentation
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        
                        // Protected endpoints - CẦN xác thực
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/dashboard/**").authenticated()
                        .requestMatchers("/api/auth/user/**").authenticated()
                        .requestMatchers("/api/auth/logout").authenticated()
                        
                        // Admin endpoints - CẦN admin role
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // Default: Tất cả request khác cần xác thức
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                );
        
        return http.build();
    }

    // Custom UserDetailsService để tắt default user
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("User not found: " + username);
        };
    }

    // Authentication Provider để JWT authentication hoạt động
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

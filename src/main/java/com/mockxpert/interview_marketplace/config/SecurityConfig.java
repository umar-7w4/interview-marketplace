package com.mockxpert.interview_marketplace.config;

import com.google.firebase.auth.FirebaseAuth;
import com.mockxpert.interview_marketplace.filters.FirebaseAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Security Configuration Loaded - Securing Endpoints");

        http
            // Disable CSRF since we are using stateless authentication
            .csrf(csrf -> csrf.disable())
            // Configure endpoint access rules
            .authorizeHttpRequests(auth -> auth
                // Permit unauthenticated access to authentication endpoints
                .requestMatchers(
                        "/api/auth/login", 
                        "/api/auth/register", 
                        "/api/auth/forgot-password", 
                        "/api/auth/change-password", 
                        "/api/auth/reset-password"
                ).permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            // Set session management to stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Add our custom Firebase authentication filter into the filter chain
            .addFilterBefore(new FirebaseAuthenticationFilter(firebaseAuth), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    /**
     * Defines a PasswordEncoder bean using BCrypt for secure password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

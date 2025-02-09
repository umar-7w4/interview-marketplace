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
                        "/api/users/login", 
                        "/api/users/register", 
                        "/api/users/forgot-password", 
                        "/api/users/change-password", 
                        "/api/users/reset-password",
                        "/api/verification/sendOtp",
                        "/api/users/verification/sendOtp",      
                        "/api/users/verification/verifyOtp",   
                        "/api/users/verification/resendOtp",
                        "/api/payments/success",  
                        "/api/payments/cancel"    
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

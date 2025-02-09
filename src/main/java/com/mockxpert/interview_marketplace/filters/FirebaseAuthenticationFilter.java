package com.mockxpert.interview_marketplace.filters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final FirebaseAuth firebaseAuth;
    // Define endpoints that should not require authentication.
    private final List<AntPathRequestMatcher> skipAuthMatchers;

    public FirebaseAuthenticationFilter(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        this.skipAuthMatchers = new ArrayList<>();
        // Adjust the paths and HTTP methods as needed
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/users/login", "POST"));
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/users/register", "POST"));
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/users/forgot-password", "POST"));
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/users/change-password", "POST"));
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/users/reset-password", "POST"));
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/verification/sendOtp", "POST"));
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/verification/verifyOtp", "POST"));
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/verification/resendOtp", "POST"));
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/payments/success", "GET"));
        skipAuthMatchers.add(new AntPathRequestMatcher("/api/payments/cancel", "GET"));

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        for (AntPathRequestMatcher matcher : skipAuthMatchers) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            System.err.println("Missing or invalid Authorization header.");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(7);
        System.out.println("Received token: " + token);

        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
            System.out.println("Decoded token UID: " + decodedToken.getUid());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(decodedToken.getUid(), null, new ArrayList<>());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("SecurityContextHolder updated with UID: " + decodedToken.getUid());

        } catch (FirebaseAuthException e) {
            System.err.println("Token verification failed: " + e.getMessage());
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

}

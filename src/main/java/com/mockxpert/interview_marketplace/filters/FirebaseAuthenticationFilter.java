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
        // Retrieve and validate the Authorization header.
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(7);
        try {
            // Verify the token with Firebase. This automatically checks the signature and expiry.
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
            // Set an Authentication object in the SecurityContext.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(decodedToken.getUid(), null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (FirebaseAuthException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}

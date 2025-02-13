package com.mockxpert.interview_marketplace.filters;

import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * 
 * Filter that filters all the http requests and adds a initial log to the request.
 * 
 * @author Umar Mohammad
 * 
 */

@Component
public class LoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Incoming request: " + request.getMethod() + " " + request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}

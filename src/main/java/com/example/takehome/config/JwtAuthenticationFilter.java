package com.example.takehome.config;

import com.example.takehome.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 Filter class for handling JWT authentication.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    public final UserDetailsService userDetailService;

    /**
     Filters the request and checks if the request contains a valid JWT token.
     @param request http request
     @param response http response
     @param filterChain filter chain
     @throws ServletException servlet exception
     @throws IOException io exception
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Get the auth header from the "Authorization" bearer header
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No auth bearer token header, continue with default behavior
            filterChain.doFilter(request, response);
            return;
        }

        // Get jwtToken from http request header
        jwtToken = authHeader.substring(7);
        userEmail = jwtTokenService.extractUserName(jwtToken);

        // user is not authenticated yet, but we have a token and try to will authenticate him now
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user from DB by email
            UserDetails userDetails = userDetailService.loadUserByUsername(userEmail);

            // Check if the token can be decoded with our secret key and
            // if the user from DB matches the user from the token and
            // if the token is not expired
            if (jwtTokenService.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // setting this token in the context means that the user becomes authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}

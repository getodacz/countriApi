package com.example.takehome.service;

import com.example.takehome.dto.auth.AuthenticationRequest;
import com.example.takehome.dto.auth.AuthenticationResponse;
import com.example.takehome.model.User;
import com.example.takehome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 This class provides the functionality to authenticate a user based on email and password
 by using an instance of AuthenticationManager, UserRepository and JwtTokenService.
 The class is marked as a Service and is expected to be used in the context of a Spring
 application. It also uses Lombok's RequiredArgsConstructor annotation to generate a constructor
 that injects the dependencies.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates the user by verifying the email and password provided in the {@link AuthenticationRequest}.
     *
     * @param request the {@link AuthenticationRequest} object containing the user's email and password
     * @return an {@link AuthenticationResponse} object containing a JWT token
     * @throws AuthenticationException if authentication fails
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate the user (verify email and pwd)
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        String jwtToken = jwtTokenService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}

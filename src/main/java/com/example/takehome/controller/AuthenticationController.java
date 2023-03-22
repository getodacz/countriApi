package com.example.takehome.controller;

import com.example.takehome.dto.auth.AuthenticationRequest;
import com.example.takehome.dto.auth.AuthenticationResponse;
import com.example.takehome.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 Controller class for handling user authentication requests.
 */
@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    /**
     Authenticates a user and returns a JWT token.
     @param request authentication request containing username and password
     @return response entity containing the JWT token
     */
    @PostMapping ("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}

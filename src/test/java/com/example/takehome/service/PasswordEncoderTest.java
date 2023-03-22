package com.example.takehome.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * This class is used to test the password encoder.
 */
public class PasswordEncoderTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * This test method is used to test the password encoder.
     * It should encode the password.
     */
    @Test
    public void testPasswordEncoder() {
        String plainPassword = "p11111!";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        System.out.println("Encoded password: " + encodedPassword);
        assertTrue(passwordEncoder.matches(plainPassword, encodedPassword));
    }
}

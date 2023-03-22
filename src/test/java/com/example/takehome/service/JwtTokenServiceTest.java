package com.example.takehome.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * This class is used to test the JWT token service.
 */
@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(classes = JwtTokenService.class)
public class JwtTokenServiceTest {

    @Mock
    private UserDetails userDetails;
    @Autowired
    private JwtTokenService jwtTokenService = new JwtTokenService();;

    @Before("")
    public void setUp() {
        jwtTokenService.setJwtTokenExpirationTime(10000L);
        jwtTokenService.setJwtTokenSecretKey("secret-key");
    }

    /**
     * This test method is used to test the JWT token service.
     * It should generate a JWT token.
     */
    @Test
    public void testExtractUserName() {
        String jwtToken = Jwts.builder()
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenService.getJwtTokenExpirationTime()))
                .signWith(jwtTokenService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        String username = jwtTokenService.extractUserName(jwtToken);
        assertEquals("user", username);
    }

    /**
     * This test method is used to test the JWT token service.
     * It should generate a JWT token.
     */
    @Test
    public void testExtractClaim() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "John");
        claims.put("age", 30);

        String jwtToken = Jwts.builder()
                .setClaims(claims)
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenService.getJwtTokenExpirationTime()))
                .signWith(jwtTokenService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        String name = jwtTokenService.extractClaim(jwtToken, claimsResolver -> claimsResolver.get("name", String.class));
        assertEquals("John", name);

        Integer age = jwtTokenService.extractClaim(jwtToken, claimsResolver -> claimsResolver.get("age", Integer.class));
        assertEquals(Integer.valueOf(30), age);
    }

    /**
     * This test method is used to test the JWT token service.
     * It should generate a JWT token.
     */
    @Test
    public void testGenerateToken() {
        String jwtToken = jwtTokenService.generateToken(userDetails);
        assertNotNull(jwtToken);
    }

    /** This test method is used to test the JWT token service.
     * It should generate a JWT token.
     */
    @Test
    public void testGenerateTokenWithClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "John");
        claims.put("age", 30);

        String jwtToken = jwtTokenService.generateToken(claims, userDetails);
        assertNotNull(jwtToken);
    }

    /**
     * This test method is used to test the JWT token service.
     * It should generate a JWT token.
     */
    @Test
    public void testIsTokenValid() {
        String jwtToken = Jwts.builder()
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenService.getJwtTokenExpirationTime()))
                .signWith(jwtTokenService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        when(userDetails.getUsername()).thenReturn("user");

        boolean isTokenValid = jwtTokenService.isTokenValid(jwtToken, userDetails);
        assertTrue(isTokenValid);
    }

    /**
     * This test method is used to test the JWT token service.
     * It should generate a JWT token.
     */
    @Test
    public void testTokenAcceptedBeforeExpired() {
        String jwtToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(
                        System.currentTimeMillis() - jwtTokenService.getJwtTokenExpirationTime()))
                .signWith(jwtTokenService.getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        try {
            jwtTokenService.isTokenExpired(jwtToken);
            fail("Token not expired");
        } catch(Exception e) {
            assertTrue(e instanceof ExpiredJwtException);
        }
    }
}
package com.example.takehome.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
/**
 This service class provides methods to generate and verify JWT tokens for authentication
 purposes using the io.jsonwebtoken library.
 */
@Service
@Getter
@Setter
public class JwtTokenService {
    /**
     * The expiration time of the JWT token in milliseconds, obtained from the application
     * properties file.
     */
    @Value("${jwtToken.expiration-time}")
    private long jwtTokenExpirationTime;

    /**
     * The secret key used for signing the JWT token, obtained from the application properties
     * file.
     */
    @Value("${jwtToken.secret-key}")
    private String jwtTokenSecretKey;

    /**
     * Extracts the username from a JWT token.
     *
     * @param jwtToken The JWT token from which the username is to be extracted.
     * @return The username extracted from the JWT token.
     */
    // Extract a particular claim (called subject) from the JWT token
    public String extractUserName(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    /**
     * Extracts a particular claim from a JWT token using a functional interface.
     *
     * @param jwtToken       The JWT token from which the claim is to be extracted.
     * @param claimsResolver The functional interface used to resolve the claim.
     * @param <T>            The type of the claim to be extracted.
     * @return The extracted claim of the specified type.
     */
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JSON Web Token (JWT) without any extra claims, only including the
     * username from the provided {@code userDetails} object.
     *
     * @param userDetails the user details object from which to extract the username
     * @return a JWT string representing the user
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JSON Web Token (JWT) including extra claims and the username from
     * the provided {@code userDetails} object.
     *
     * @param extraClaims  a map of extra claims to include in the JWT
     * @param userDetails the user details object from which to extract the username
     * @return a JWT string representing the user and any extra claims
     */
    public String generateToken(Map<String, Object> extraClaims,
                                UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(
                        System.currentTimeMillis() + jwtTokenExpirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Checks if a given JWT token is valid for a given user.
     *
     * @param jwtToken     the JWT token to check
     * @param userDetails the user details object to compare against the JWT
     * @return {@code true} if the token is valid for the user, {@code false} otherwise
     */
    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUserName(jwtToken);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(jwtToken);
    }

    /**
     * Checks if a given JWT token has expired.
     *
     * @param token the JWT token to check
     * @return {@code true} if the token has expired, {@code false} otherwise
     */
    public boolean isTokenExpired(String token) {
        // Check if the token expiration time is before current time
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration time from a given JWT token.
     *
     * @param token the JWT token from which to extract the expiration time
     * @return a {@code Date} object representing the expiration time of the token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from a given JWT token.
     *
     * @param jwtToken the JWT token from which to extract claims
     * @return a {@code Claims} object representing all claims from the token
     */
    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    /**
     * Retrieves the signing key used to generate JWT tokens.
     *
     * @return a {@code Key} object representing the signing key
     */
    public Key getSignInKey() {
        // base64 decode
        byte[] keyBytes = Decoders.BASE64.decode(jwtTokenSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

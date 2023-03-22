package com.example.takehome.generators;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class provides a method to generate a secret key for signing JWT tokens.
 */
public class JwtSecretKeyGeneratorTest {
    /**
     * Generates a secret key for signing JWT tokens.
     */
    @Test
    public void generateSecretKey() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        String secretString = bytesToHex(key.getEncoded());
        System.out.println("Generated Secret Key: " + secretString);

        byte[] keyBytes = Decoders.BASE64.decode(secretString);
        Key secretKeySpec = Keys.hmacShaKeyFor(keyBytes);

        String jwtToken = Jwts.builder().setSubject("test").signWith(secretKeySpec).compact();
        System.out.println("Generated JWT token: " + jwtToken);
        assertNotNull(jwtToken);
    }

    /**
     * Utility method to convert byte array to hex string.
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
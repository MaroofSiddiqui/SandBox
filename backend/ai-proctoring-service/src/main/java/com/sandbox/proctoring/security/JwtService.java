package com.sandbox.proctoring.security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;

    public JwtService(
            @Value("${app.jwt.secret}") String jwtSecret) {

        /*
         * IMPORTANT:
         *
         * Auth Service treats JWT_SECRET as a
         * Base64-encoded secret.
         *
         * Therefore AI Proctoring Service MUST
         * decode it in exactly the same way.
         */
        byte[] keyBytes =
                Decoders.BASE64.decode(jwtSecret);

        this.signingKey =
                Keys.hmacShaKeyFor(keyBytes);
    }

    /*
     * Parse + validate token.
     *
     * JJWT automatically verifies:
     * - signature
     * - expiration
     * - token structure
     */
    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    public Long extractUserId(String token) {

        Number userId =
                extractAllClaims(token)
                        .get("userId", Number.class);

        return userId != null
                ? userId.longValue()
                : null;
    }

    public Long extractOrganizationId(String token) {

        Number organizationId =
                extractAllClaims(token)
                        .get("organizationId", Number.class);

        return organizationId != null
                ? organizationId.longValue()
                : null;
    }
}
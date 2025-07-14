package com.aixo.userservice.provider;

import com.aixo.userservice.config.AppConfig;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // in seconds

    /**
     * Generates a signed JWT from the given OAuth2 user details.
     */
    public String generateToken(OAuth2User user) {
        String email = getAttribute(user, AppConfig.OAuth2.ATTRIBUTE_EMAIL);
        String name = getAttribute(user, AppConfig.OAuth2.ATTRIBUTE_NAME);
        String oAuthId = getAttribute(user, AppConfig.OAuth2.ATTRIBUTE_SUB);

        Instant now = Instant.now();

        return Jwts.builder()
                .claim(AppConfig.Jwt.EMAIL, email)
                .claim(AppConfig.Jwt.NAME, name)
                .claim(AppConfig.Jwt.OAUTH_ID, oAuthId)
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(jwtExpiration)))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Validates the given JWT and returns the claims.
     */
    public Map<String, Object> validateToken(String token) throws IllegalArgumentException, JwtException {
        var jwsClaims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Map<String, Object> claims = new HashMap<>();
        claims.put(AppConfig.Jwt.NAME, jwsClaims.get(AppConfig.Jwt.NAME));
        claims.put(AppConfig.Jwt.EMAIL, jwsClaims.get(AppConfig.Jwt.EMAIL));
        claims.put(AppConfig.Jwt.OAUTH_ID, jwsClaims.get(AppConfig.Jwt.OAUTH_ID));
        claims.put(AppConfig.Jwt.EXPIRATION, jwsClaims.getIssuedAt());
        claims.put(AppConfig.Jwt.ISSUED_AT, jwsClaims.getExpiration());

        return claims;
    }

    /**
     * Extracts a required attribute from the OAuth2User.
     */
    private String getAttribute(OAuth2User user, String attribute) {
        return Optional.ofNullable(user.getAttribute(attribute))
                .map(Object::toString)
                .orElseThrow(() -> new IllegalArgumentException("Missing OAuth2 attribute: " + attribute));
    }

    /**
     * Builds the secret key used to sign/verify the JWT.
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}

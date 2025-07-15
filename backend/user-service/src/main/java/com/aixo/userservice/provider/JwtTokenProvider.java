package com.aixo.userservice.provider;

import com.aixo.userservice.config.AppConfig;
import com.aixo.userservice.model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // in seconds

    /**
     * Generates a signed JWT from the given OAuth2 user details.
     */
    public String generateToken(User user) {

        Instant now = Instant.now();

        return Jwts.builder()
                .claim(AppConfig.Jwt.EMAIL, user.getEmail())
                .claim(AppConfig.Jwt.NAME, user.getName())
                .claim(AppConfig.Jwt.OAUTH_ID, user.getOAuthId())
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
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
        claims.put(AppConfig.Jwt.ID, jwsClaims.getId());
        claims.put(AppConfig.Jwt.NAME, jwsClaims.get(AppConfig.Jwt.NAME));
        claims.put(AppConfig.Jwt.EMAIL, jwsClaims.get(AppConfig.Jwt.EMAIL));
        claims.put(AppConfig.Jwt.OAUTH_ID, jwsClaims.get(AppConfig.Jwt.OAUTH_ID));
        claims.put(AppConfig.Jwt.EXPIRATION, jwsClaims.getIssuedAt());
        claims.put(AppConfig.Jwt.ISSUED_AT, jwsClaims.getExpiration());

        return claims;
    }

    /**
     * Builds the secret key used to sign/verify the JWT.
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}

package com.aixo.userservice.provider;

import com.aixo.userservice.config.AppConfig;
import com.aixo.userservice.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
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
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }
}

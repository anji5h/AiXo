package com.aixo.apigateway.provider;

import com.aixo.apigateway.config.AppConfig;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private final RedisTemplate<String, String> redisTemplate;

    public JwtTokenProvider(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, Object> validateToken(String token) throws IllegalArgumentException, JwtException {
        var jwsClaims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (redisTemplate.hasKey(AppConfig.Redis.DENYLIST_PREFIX + jwsClaims.getId())) {
            throw new JwtException("jwt token has expired");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(AppConfig.Jwt.NAME, jwsClaims.get(AppConfig.Jwt.NAME));
        claims.put(AppConfig.Jwt.EMAIL, jwsClaims.get(AppConfig.Jwt.EMAIL));
        claims.put(AppConfig.Jwt.OAUTH_ID, jwsClaims.get(AppConfig.Jwt.OAUTH_ID));

        return claims;
    }
}

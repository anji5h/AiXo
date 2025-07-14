package com.aixo.userservice.filter;

import com.aixo.userservice.config.AppConfig;
import com.aixo.userservice.provider.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   RedisTemplate<String, String> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromHeader(request);

        if (!StringUtils.hasText(token)) {
            sendUnauthorized(response, "Missing JWT token");
            return;
        }

        try {
            Map<String, Object> claims = jwtTokenProvider.validateToken(token);

            String jti = (String) claims.get(AppConfig.Jwt.ID);
            if (isTokenBlacklisted(jti)) {
                sendUnauthorized(response, "Token has been revoked");
                return;
            }

            String email = (String) claims.get(AppConfig.Jwt.EMAIL);
            if (email == null) {
                sendUnauthorized(response, "Invalid token payload");
                return;
            }

            var authentication = new UsernamePasswordAuthenticationToken(
                    email, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException ex) {
            logger.warn("JWT validation failed: " + ex.getMessage());
            sendUnauthorized(response, "Invalid or expired JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenBlacklisted(String jti) {
        if (!StringUtils.hasText(jti)) return false;
        return redisTemplate.hasKey(AppConfig.Redis.DENYLIST_PREFIX + jti);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader(AppConfig.Http.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(AppConfig.Http.BEARER_PREFIX)) {
            return authHeader.substring(AppConfig.Http.BEARER_PREFIX.length());
        }
        return null;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = String.format("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}",
                message);
        response.getWriter().write(body);
    }
}

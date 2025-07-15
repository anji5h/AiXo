package com.aixo.apigateway.filter;

import com.aixo.apigateway.config.AppConfig;
import com.aixo.apigateway.provider.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final List<String> EXCLUDED_PATHS = Arrays.asList("/actuator", "/oauth2", "/login",
            "/swagger-ui", "/v3/api-docs");

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String path = exchange.getRequest().getURI().getPath();

        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(AppConfig.Http.BEARER_PREFIX)) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        final String token = authHeader.substring(AppConfig.Http.BEARER_PREFIX.length());
        return ProcessToken(exchange, chain, token);
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> ProcessToken(ServerWebExchange exchange, GatewayFilterChain chain, String token) {
        try {
            final Map<String, Object> claims = jwtTokenProvider.validateToken(token);
            return chain.filter(
                    exchange.mutate().request(addUserHeaders(exchange.getRequest(), claims)).build());
        } catch (Exception ex) {
            return unauthorizedResponse(exchange, ex.getMessage());
        }
    }

    private ServerHttpRequest addUserHeaders(ServerHttpRequest request, Map<String, Object> claims) {
        return request.mutate().header("X-User-Id", claims.get(AppConfig.Jwt.OAUTH_ID).toString())
                .header("X-User-Email", claims.get(AppConfig.Jwt.EMAIL).toString())
                .header("X-User-Name", claims.get(AppConfig.Jwt.NAME).toString()).build();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String reason) {
        logger.warn("Authentication failed: {}", reason);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

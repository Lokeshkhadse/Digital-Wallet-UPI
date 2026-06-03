package com.example.Api_Gateway.security;

import com.example.Api_Gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_APIS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh"
    );

    public JwtGlobalFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // allow public APIs
        if (PUBLIC_APIS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.extractClaims(token);

            String user = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);

            // 🔥 PASS ONLY TRUSTED DATA
            var modifiedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-USER", user)
                    .header("X-ROLES", String.join(",", roles))
                    .build();

            return chain.filter(exchange.mutate()
                    .request(modifiedRequest)
                    .build());

        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
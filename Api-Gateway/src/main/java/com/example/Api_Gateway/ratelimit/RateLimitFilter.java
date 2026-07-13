package com.example.Api_Gateway.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.example.Api_Gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;

@Component
@RequiredArgsConstructor
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final RateLimiterService rateLimiterService;
    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_APIS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/swagger-ui",           // Swagger UI के लिए
            "/webjars",              // UI एसेट्स के लिए
            "/v3/api-docs"           // गेटवे की अपनी डॉक्स के लिए
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        System.out.println("RateLimitFilter executed");

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        /*
            PUBLIC API

            No JWT

            Use Client IP
         */

        if (PUBLIC_APIS.stream().anyMatch(path::startsWith) ) {

            String clientIp = getClientIp(exchange);

            Bucket bucket =
                    rateLimiterService.resolveIpBucket(clientIp);

            ConsumptionProbe probe =
                    bucket.tryConsumeAndReturnRemaining(1);

            if (!probe.isConsumed()) {

                return rateLimitExceeded(exchange);
            }

            return chain.filter(exchange);
        }

      /*
    Protected APIs

    JWT Required

    Use Email as Bucket Key
 */

        String authHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            return unauthorized(exchange);
        }

        String token =
                authHeader.substring(7);

        try {

            Claims claims =
                    jwtUtil.extractClaims(token);

            exchange.getAttributes().put("claims", claims);

            String email =
                    claims.getSubject();

            Bucket bucket =
                    rateLimiterService.resolveUserBucket(email);

            ConsumptionProbe probe =
                    bucket.tryConsumeAndReturnRemaining(1);

            if (!probe.isConsumed()) {

                return rateLimitExceeded(exchange);
            }

        }
        catch (Exception ex) {

            return unauthorized(exchange);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> rateLimitExceeded(
            ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

        exchange.getResponse()
                .getHeaders()
                .add(HttpHeaders.CONTENT_TYPE,
                        "application/json");

        byte[] bytes = """
                {
                  "status":429,
                  "message":"Too Many Requests. Please try again after one minute."
                }
                """.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse()
                .writeWith(
                        Mono.just(
                                exchange.getResponse()
                                        .bufferFactory()
                                        .wrap(bytes)
                        )
                );
    }

    private String getClientIp(
            ServerWebExchange exchange) {

        if (exchange.getRequest()
                .getRemoteAddress() == null) {

            return "UNKNOWN";
        }

        return exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();
    }


    private Mono<Void> unauthorized(
            ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse()
                .setComplete();
    }

    @Override
    public int getOrder() {

        /*
            Runs before JwtGlobalFilter
         */

        return -2;
    }
}
package com.example.Api_Gateway.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    public Bucket newBucket() {

        Bandwidth limit =
                Bandwidth.classic(                      //create bucket with 20 allow (every 1 minute it refill 20)
                        20,
                        Refill.intervally(
                                20,
                                Duration.ofMinutes(1)));

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
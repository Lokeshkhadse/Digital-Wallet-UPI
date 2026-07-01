package com.example.Api_Gateway.ratelimit;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RateLimitConfig rateLimitConfig;

    /*
        Protected APIs

        Key = User Email (X-USER)

        Example

        lokesh@gmail.com  -> Bucket
        admin@gmail.com   -> Bucket
        abc@gmail.com     -> Bucket
     */

    private final ConcurrentHashMap<String, Bucket> userBuckets =
            new ConcurrentHashMap<>();


    /*
        Public APIs

        Key = Client IP

        Example

        192.168.1.10 -> Bucket
        192.168.1.20 -> Bucket
     */

    private final ConcurrentHashMap<String, Bucket> ipBuckets =
            new ConcurrentHashMap<>();


    /**
     * Bucket for authenticated users
     */
    public Bucket resolveUserBucket(String email) {

        return userBuckets.computeIfAbsent(
                email,
                key -> rateLimitConfig.newBucket());
    }


    /**
     * Bucket for public APIs
     */
    public Bucket resolveIpBucket(String ipAddress) {

        return ipBuckets.computeIfAbsent(
                ipAddress,
                key -> rateLimitConfig.newBucket());
    }

}

//Ye internally kaise kaam karega?
//Pehli baar request
//        Email
//
//lokesh@gmail.com
//
//        Map
//
//Empty
//
//↓
//
//computeIfAbsent()
//
//↓
//
//Bucket Create
//
//20 Tokens
//
//Map ban gaya
//
//lokesh@gmail.com
//
//↓
//
//Bucket(20)
//Second Request
//lokesh@gmail.com
//
//Ab kya hoga?
//
//Map already contain karta hai
//
//lokesh@gmail.com
//
//↓
//
//Bucket(19)
//
//Naya bucket nahi banega.
//
//Same bucket return hoga.
//
//        Dusra user
//admin@gmail.com
//
//        Map
//
//lokesh@gmail.com -> Bucket
//
//admin@gmail.com ?
//
//Not Found
//
//↓
//
//Naya Bucket create
//
//lokesh@gmail.com -> Bucket
//
//admin@gmail.com -> Bucket
//
//Ab dono completely independent hain.
//
//        Public APIs
//
//Suppose
//
//POST /auth/login
//
//JWT nahi hai.
//
//To email nahi milega.
//
//Tab
//
//Client IP
//
//192.168.1.15
//
//ke against bucket create hoga.
//
//192.168.1.15
//
//        ↓
//
//Bucket
//Current Flow
//RateLimitConfig
//        │
//                ▼
//Creates Bucket (20/min)
//        │
//                ▼
//RateLimiterService
//        │
//                ├── Email → Bucket
//        └── IP → Bucket
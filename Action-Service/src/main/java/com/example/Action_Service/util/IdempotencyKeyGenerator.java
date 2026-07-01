package com.example.Action_Service.util;

import java.util.UUID;

public class IdempotencyKeyGenerator {

    private IdempotencyKeyGenerator() {}


    public static String generate() {
        return "TXN-Idem-" + UUID.randomUUID();
    }
}
package com.example.Action_Service.feign;

import com.example.Action_Service.dto.LedgerRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LedgerServiceFallback implements LedgerServiceClient {

    @Override
    public Map<String, Object> createLedgerEntry(LedgerRequest request) {

        return Map.of(
                "status", "FAILED",
                "message", "Ledger Service is DOWN - fallback executed"
        );
    }
}
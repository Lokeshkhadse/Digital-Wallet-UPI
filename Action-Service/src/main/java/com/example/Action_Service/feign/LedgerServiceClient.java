package com.example.Action_Service.feign;

import com.example.Action_Service.dto.LedgerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

//@FeignClient(name="ledger-service", url="http://localhost:8083")
@FeignClient(name = "LEDGER-SERVICE")
public interface LedgerServiceClient {

    @PostMapping("ledger/create")
    public Map<String, Object> createLedgerEntry(
            @RequestBody LedgerRequest request);
}

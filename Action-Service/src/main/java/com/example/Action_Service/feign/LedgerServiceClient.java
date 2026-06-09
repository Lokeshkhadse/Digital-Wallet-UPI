package com.example.Action_Service.feign;

import com.example.Action_Service.dto.LedgerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


//
//@FeignClient(
//        name = "LEDGER-SERVICE"
//       // , fallback = LedgerServiceFallback.class
//)
//public interface LedgerServiceClient {
//
//    @PostMapping("ledger/create")
//    Map<String, Object> createLedgerEntry(@RequestBody LedgerRequest request);
//}


@FeignClient(name = "LEDGER-SERVICE")
public interface LedgerServiceClient {
    @PostMapping("ledger/create")
    Map<String, Object> createLedgerEntry(@RequestBody LedgerRequest request);
}
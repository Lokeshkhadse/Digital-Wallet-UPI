package com.example.Action_Service.feign;

import com.example.Action_Service.dto.QrLookupResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(
//        name = "QUERY-SERVICE"
//)
@FeignClient(name = "QUERY-SERVICE" , url = "http://localhost:8084")
public interface QueryServiceClient {

    @GetMapping("/query/qr/lookup/{upiId}")
    QrLookupResponse lookupQr(
            @PathVariable("upiId")
            String upiId);
}
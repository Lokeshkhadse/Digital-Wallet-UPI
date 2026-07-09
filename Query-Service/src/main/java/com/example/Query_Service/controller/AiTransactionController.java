package com.example.Query_Service.controller;

import com.example.Query_Service.dto.ai.AiSearchRequest;
import com.example.Query_Service.dto.ai.AiSearchResponse;
import com.example.Query_Service.service.ai.AiSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/query/ai")
@RequiredArgsConstructor
public class AiTransactionController {

    private final AiSearchService aiSearchService;

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Object> search(
            @RequestBody AiSearchRequest request) {

        return ResponseEntity.ok(
                aiSearchService.understandQuestion(request));
    }
}
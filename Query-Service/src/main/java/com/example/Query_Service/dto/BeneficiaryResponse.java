package com.example.Query_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryResponse {

    private Long id;

    private Long userId;

    private Long beneficiaryUserId;

    private String nickname;

    private LocalDateTime createdAt;
}

package com.example.Action_Service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BeneficiaryRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long beneficiaryUserId;

    private String nickname;
}

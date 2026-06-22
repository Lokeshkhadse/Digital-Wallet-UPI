package com.example.Action_Service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FraudResult {

    private boolean suspicious;

    private String reason;
}

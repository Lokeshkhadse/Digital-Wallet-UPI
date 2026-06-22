package com.example.Query_Service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SplitBillActivityResponse {

    private Long activityId;

    private Long userId;

    private String activityType;

    private String remarks;

    private LocalDateTime createdAt;
}
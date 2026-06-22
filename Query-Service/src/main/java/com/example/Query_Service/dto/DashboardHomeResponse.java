package com.example.Query_Service.dto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardHomeResponse {

    private Long userId;

    private BigDecimal totalBalance;

    private List<AccountSummaryResponse> linkedAccounts;
}
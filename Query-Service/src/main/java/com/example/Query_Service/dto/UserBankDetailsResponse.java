package com.example.Query_Service.dto;


import com.example.Query_Service.enums.AccountType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBankDetailsResponse {

    private Long id;

    private Long userId;

    private String bankName;

    private String accountNumber;

    private String ifscCode;

    private String upiId;

    private AccountType accountType;
}

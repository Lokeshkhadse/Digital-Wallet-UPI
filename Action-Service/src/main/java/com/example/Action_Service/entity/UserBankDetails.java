package com.example.Action_Service.entity;

import com.example.Action_Service.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_bank_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserBankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String upiId;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private LocalDateTime createdAt;
}

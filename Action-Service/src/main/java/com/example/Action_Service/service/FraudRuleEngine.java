package com.example.Action_Service.service;

import com.example.Action_Service.dto.FraudResult;
import com.example.Action_Service.entity.Transaction;
import com.example.Action_Service.enums.TransactionStatus;
import com.example.Action_Service.enums.TransactionType;
import com.example.Action_Service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudRuleEngine {

    private final TransactionRepository transactionRepository;

    public FraudResult checkFraud(
            Long userId,
            Long userBankId,
            BigDecimal amount) {

        // Rule 1
        if(amount.compareTo(
                BigDecimal.valueOf(50000)) > 0){

            return FraudResult.builder()
                    .suspicious(true)
                    .reason("Transfer amount exceeds ₹50,000")
                    .build();
        }

        // Rule 2
        Long count =
                transactionRepository
                        .countRecentTransactions(
                                userId,
                                LocalDateTime.now()
                                        .minusMinutes(1));

        if(count >= 10){

            return FraudResult.builder()
                    .suspicious(true)
                    .reason("More than 10 transactions in 1 minute")
                    .build();
        }

        return FraudResult.builder()
                .suspicious(false)
                .reason("No Fraud Detected")
                .build();
    }
}
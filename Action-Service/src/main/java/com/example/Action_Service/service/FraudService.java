package com.example.Action_Service.service;

import com.example.Action_Service.dto.FraudResult;
import com.example.Action_Service.entity.FraudAlert;
import com.example.Action_Service.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudService {

    private final FraudRuleEngine ruleEngine;
    private final FraudAlertRepository fraudAlertRepository;

    public void fraudCheck(
            Long userId,
            Long userBankId,
            BigDecimal amount) {

        FraudResult result =
                ruleEngine.checkFraud(
                        userId,
                        userBankId,
                        amount);

        if(result.isSuspicious()) {

            FraudAlert alert =
                    new FraudAlert();

            alert.setUserId(userId);
            alert.setUserBankId(userBankId);
            alert.setAmount(amount);
            alert.setFraudType("SUSPICIOUS_TRANSACTION");
            alert.setReason(result.getReason());
            alert.setStatus("OPEN");
            alert.setCreatedAt(
                    LocalDateTime.now());

            fraudAlertRepository.save(alert);
        }
    }
}
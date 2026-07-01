package com.example.Action_Service.mapper;

import com.example.Action_Service.entity.QrPayment;
import com.example.Action_Service.entity.UserBankDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class QrPaymentMapper {

    public QrPayment toEntity(
            UserBankDetails bank){

        return QrPayment.builder()
                .userId(bank.getUserId())
                .userBankId(bank.getId())
                .upiId(bank.getUpiId())
                .qrContent(bank.getUpiId())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
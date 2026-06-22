package com.example.Query_Service.service;

import com.example.Query_Service.dto.*;
//import com.example.Query_Service.entity.QrPayment;
import com.example.Query_Service.entity.UserBankDetails;
import com.example.Query_Service.exception.QrNotFoundException;
//import com.example.Query_Service.mapper.QrPaymentMapper;
//import com.example.Query_Service.repository.QrPaymentRepository;
import com.example.Query_Service.repository.UserBankDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QrPaymentService {

    private final UserBankDetailsRepository bankRepository;



    public QrLookupResponse lookupQr(
            String upiId) {

        UserBankDetails bank =
                bankRepository.findByUpiId(upiId)
                        .orElseThrow(() ->
                                new QrNotFoundException(
                                        "UPI not found"));

        return QrLookupResponse.builder()
                .userId(bank.getUserId())
                .userBankId(bank.getId())
                .upiId(bank.getUpiId())
                .bankName(bank.getBankName())
                .accountNumber(
                        bank.getAccountNumber())
                .accountHolderName(
                        "User-" + bank.getUserId())
                .build();
    }
}
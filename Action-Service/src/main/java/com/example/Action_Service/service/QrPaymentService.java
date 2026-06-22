package com.example.Action_Service.service;

import com.example.Action_Service.dto.*;
import com.example.Action_Service.entity.QrPayment;
import com.example.Action_Service.entity.UserBankDetails;
import com.example.Action_Service.feign.QueryServiceClient;
import com.example.Action_Service.mapper.QrPaymentMapper;
import com.example.Action_Service.repository.QrPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QrPaymentService {

    private final QueryServiceClient queryServiceClient;
    private final TransferService transferService;
    private final QrPaymentRepository repository;
    private final QrPaymentMapper mapper;


    public TransferTransactionResponse payUsingQr(
            ScanQrPaymentRequest request){

        QrLookupResponse qrData =
                queryServiceClient.lookupQr(
                        request.getUpiId());

        TransferTransactionRequest transferRequest =
                TransferTransactionRequest.builder()
                        .senderUserId(
                                request.getSenderUserId())
                        .senderBankId(
                                request.getSenderBankId())

                        .receiverUserId(
                                qrData.getUserId())

                        .receiverBankId(
                                qrData.getUserBankId())

                        .amount(
                                request.getAmount())

                        .upiPin(
                                request.getUpiPin())

                        .remarks(
                                request.getRemarks())

                        .transactionType(
                                "TRANSFER")

                        .build();

        return transferService.transfer(
                transferRequest);
    }


    public void generateQr(
            UserBankDetails bank){

        if(repository.existsByUpiId(
                bank.getUpiId())){
            return;
        }

        QrPayment qr =
                mapper.toEntity(bank);

        repository.save(qr);
    }
}
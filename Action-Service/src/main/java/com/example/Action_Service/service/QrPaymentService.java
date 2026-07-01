package com.example.Action_Service.service;

import com.example.Action_Service.dto.*;
import com.example.Action_Service.entity.QrPayment;
import com.example.Action_Service.entity.UserBankDetails;
import com.example.Action_Service.feign.QueryServiceClient;
import com.example.Action_Service.mapper.QrPaymentMapper;
import com.example.Action_Service.repository.QrPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrPaymentService {

    private final QueryServiceClient queryServiceClient;
    private final TransferService transferService;
    private final QrPaymentRepository repository;
    private final QrPaymentMapper mapper;


    public TransferTransactionResponse payUsingQr(
            ScanQrPaymentRequest request) {

        log.info(
                "QR payment initiated | SenderUserId={} | SenderBankId={} | UPI={}",
                request.getSenderUserId(),
                request.getSenderBankId(),
                request.getUpiId());

        QrLookupResponse qrData =
                queryServiceClient.lookupQr(
                        request.getUpiId());

        log.info(
                "QR lookup successful | ReceiverUserId={} | ReceiverBankId={}",
                qrData.getUserId(),
                qrData.getUserBankId());

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

        TransferTransactionResponse response =
                transferService.transfer(
                        transferRequest);

        log.info(
                "QR payment completed successfully | SenderUserId={} | ReceiverUserId={} | Amount={}",
                request.getSenderUserId(),
                qrData.getUserId(),
                request.getAmount());

        return response;
    }
    public void generateQr(
            UserBankDetails bank){

        log.info(
                "QR generation requested | BankId={} | UPI={}",
                bank.getId(),
                bank.getUpiId());

        if(repository.existsByUpiId(
                bank.getUpiId())){

            log.warn(
                    "QR already exists | UPI={}",
                    bank.getUpiId());

            return;
        }

        QrPayment qr =
                mapper.toEntity(bank);

        repository.save(qr);

        log.info(
                "QR generated successfully | BankId={} | UPI={}",
                bank.getId(),
                bank.getUpiId());
    }
}
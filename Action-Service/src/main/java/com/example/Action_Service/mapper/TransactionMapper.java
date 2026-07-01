package com.example.Action_Service.mapper;



import com.example.Action_Service.dto.DepositRequest;
import com.example.Action_Service.dto.TransferTransactionRequest;
import com.example.Action_Service.dto.TransferTransactionResponse;
import com.example.Action_Service.dto.WithdrawRequest;
import com.example.Action_Service.entity.Transaction;
import com.example.Action_Service.enums.TransactionStatus;
import com.example.Action_Service.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    public Transaction toEntity(
            TransferTransactionRequest request,
            String refNo) {

        return Transaction.builder()
                .transactionRefNo(refNo)
                .senderUserId(request.getSenderUserId())
                .receiverUserId(request.getReceiverUserId())
                .senderBankId(request.getSenderBankId())
                .receiverBankId(request.getReceiverBankId())
                .amount(request.getAmount())
                .transactionType(
                        TransactionType.valueOf(
                                request.getTransactionType().toUpperCase()
                        )
                )
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .remarks(request.getRemarks())
                .build();
    }

    public TransferTransactionResponse toResponse(
            Transaction tx,
            String message) {

        return TransferTransactionResponse.builder()
                .transactionRefNo(tx.getTransactionRefNo())
                .status(tx.getTransactionStatus().name())
                .amount(tx.getAmount())
                .transactionType(tx.getTransactionType())
                .message(message)
                .build();
    }

    public Transaction toDepositEntity(
            DepositRequest request,
            String refNo) {

        return Transaction.builder()
                .transactionRefNo(refNo)
                .senderUserId(request.getUserId())
                .senderBankId(request.getUserBankId())
                .receiverUserId(request.getUserId())
                .receiverBankId(request.getUserBankId())
                .amount(request.getAmount())
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Transaction toWithdrawEntity(
            WithdrawRequest request,
            String refNo) {

        return Transaction.builder()
                .transactionRefNo(refNo)
                .senderUserId(request.getUserId())
                .receiverUserId(request.getUserId())
                .receiverBankId(request.getUserBankId())
                .senderBankId(request.getUserBankId())
                .amount(request.getAmount())
                .transactionType(TransactionType.WITHDRAW)
                .transactionStatus(TransactionStatus.SUCCESS)
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


}
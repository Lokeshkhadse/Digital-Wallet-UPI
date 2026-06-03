package com.example.Query_Service.mapper;

import com.example.Query_Service.dto.TransferTransactionResponse;
import com.example.Query_Service.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransferTransactionResponse toResponse(
            Transaction transaction) {

        return TransferTransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .transactionRefNo(transaction.getTransactionRefNo())
                .senderUserId(transaction.getSenderUserId())
                .receiverUserId(transaction.getReceiverUserId())
                .amount(transaction.getAmount())
                .transactionType(
                        transaction.getTransactionType().name())
                .transactionStatus(
                        transaction.getTransactionStatus().name())
                .remarks(transaction.getRemarks())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
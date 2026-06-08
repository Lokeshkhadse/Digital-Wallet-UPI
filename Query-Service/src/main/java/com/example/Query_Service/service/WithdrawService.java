package com.example.Query_Service.service;


import com.example.Query_Service.dto.TransferTransactionResponse;
import com.example.Query_Service.enums.TransactionStatus;
import com.example.Query_Service.enums.TransactionType;
import com.example.Query_Service.mapper.TransactionMapper;
import com.example.Query_Service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    public Page<TransferTransactionResponse> getUserWithdrawals(
            Long receiverUserId,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size,
                        Sort.by("createdAt").descending());

        return repository
                .findBySenderUserIdOrReceiverUserId(
                        receiverUserId,
                        receiverUserId,
                        pageable)
                .map(mapper::toResponse)
                .map(tx -> {
                    // keep only WITHDRAW records
                    return tx.getTransactionType().equals("WITHDRAW")
                            ? tx : null;
                });
    }

    public Page<TransferTransactionResponse> getWithdrawalsByStatus(
            TransactionStatus status,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByTransactionTypeAndTransactionStatus(
                        TransactionType.WITHDRAW,
                        status,
                        pageable)
                .map(mapper::toResponse);
    }

    public Page<TransferTransactionResponse> getWithdrawalsByAmount(
            BigDecimal min,
            BigDecimal max,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByTransactionTypeAndAmountBetween(
                        TransactionType.WITHDRAW,
                        min,
                        max,
                        pageable)
                .map(mapper::toResponse);
    }

    public Page<TransferTransactionResponse> getWithdrawalsByDate(
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByTransactionTypeAndCreatedAtBetween(
                        TransactionType.WITHDRAW,
                        from,
                        to,
                        pageable)
                .map(mapper::toResponse);
    }

    public List<TransferTransactionResponse> getMiniStatement(
            Long senderUserId) {

        return repository
                .findTop10ByTransactionTypeAndSenderUserIdOrderByCreatedAtDesc(
                        TransactionType.WITHDRAW,
                        senderUserId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
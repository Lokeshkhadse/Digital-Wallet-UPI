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

@Service
@RequiredArgsConstructor
public class DepositService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    public Page<TransferTransactionResponse> getUserDeposits(
            Long userId,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending());

        return repository
                .findBySenderUserIdAndTransactionType(
                        userId,
                        TransactionType.DEPOSIT,
                        pageable)
                .map(mapper::toResponse);
    }

    public Page<TransferTransactionResponse> getDepositsByStatus(
            TransactionStatus status,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByTransactionTypeAndTransactionStatus(
                        TransactionType.DEPOSIT,
                        status,
                        pageable)
                .map(mapper::toResponse);
    }

    public Page<TransferTransactionResponse> getDepositsByAmountRange(
            BigDecimal min,
            BigDecimal max,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByTransactionTypeAndAmountBetween(
                        TransactionType.DEPOSIT,
                        min,
                        max,
                        pageable)
                .map(mapper::toResponse);
    }

    public Page<TransferTransactionResponse> getDepositsByDateRange(
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByTransactionTypeAndCreatedAtBetween(
                        TransactionType.DEPOSIT,
                        from,
                        to,
                        pageable)
                .map(mapper::toResponse);
    }
}
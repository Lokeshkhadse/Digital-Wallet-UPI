package com.example.Query_Service.service;

import com.example.Query_Service.dto.TransferTransactionResponse;
import com.example.Query_Service.entity.Transaction;
import com.example.Query_Service.enums.TransactionStatus;
import com.example.Query_Service.enums.TransactionType;
import com.example.Query_Service.exception.TransactionNotFound;
import com.example.Query_Service.mapper.TransactionMapper;
import com.example.Query_Service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    public Page<TransferTransactionResponse> getUserTransactions(
            Long senderUserId,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size,
                        Sort.by("createdAt").descending());

        return repository
                .findBySenderUserIdOrReceiverUserId(
                        senderUserId,
                        senderUserId,
                        pageable)
                .map(mapper::toResponse);
    }

    public TransferTransactionResponse getByRefNo(
            String transactionRefNo) {

        Transaction transaction =
                repository.findByTransactionRefNo(transactionRefNo)
                        .orElseThrow(() ->
                                new TransactionNotFound(
                                        "Transaction not found"));

        return mapper.toResponse(transaction);
    }

    public List<TransferTransactionResponse> miniStatement(
            Long senderUserId) {

        return repository
                .findTop10BySenderUserIdOrReceiverUserIdOrderByCreatedAtDesc(
                        senderUserId,
                        senderUserId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public Page<TransferTransactionResponse> getByStatus(
            TransactionStatus status,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByTransactionStatus(
                        status,
                        pageable)
                .map(mapper::toResponse);
    }

    public Page<TransferTransactionResponse> getByType(
            TransactionType type,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByTransactionType(
                        type,
                        pageable)
                .map(mapper::toResponse);
    }

    public Page<TransferTransactionResponse> getByDateRange(
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByCreatedAtBetween(
                        from,
                        to,
                        pageable)
                .map(mapper::toResponse);
    }
}
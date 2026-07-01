package com.example.Query_Service.repository;

import com.example.Query_Service.entity.Transaction;
import com.example.Query_Service.enums.TransactionStatus;
import com.example.Query_Service.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Optional<Transaction> findByTransactionRefNo(
            String transactionRefNo);

    Page<Transaction> findBySenderUserIdOrReceiverUserId(
            Long senderUserId,
            Long receiverUserId,
            Pageable pageable);

    List<Transaction> findTop10BySenderUserIdOrReceiverUserIdOrderByCreatedAtDesc(
            Long senderUserId,
            Long receiverUserId);

    Page<Transaction> findByTransactionStatus(
            TransactionStatus status,
            Pageable pageable);

    Page<Transaction> findByTransactionType(
            TransactionType type,
            Pageable pageable);

    Page<Transaction> findByCreatedAtBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);


    //Deposit services & Withdraw services

    Page<Transaction> findBySenderUserIdAndTransactionType(
            Long userId,
            TransactionType transactionType,
            Pageable pageable);

    Page<Transaction> findByTransactionTypeAndCreatedAtBetween(
            TransactionType transactionType,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);

    Page<Transaction> findByTransactionTypeAndAmountBetween(
            TransactionType transactionType,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable);

    Page<Transaction> findByTransactionTypeAndTransactionStatus(
            TransactionType transactionType,
            TransactionStatus status,
            Pageable pageable);


    List<Transaction> findTop10ByTransactionTypeAndSenderUserIdOrderByCreatedAtDesc(
            TransactionType transactionType,
            Long userId);

    // dashboard , particular account balance
    Long countBySenderBankIdOrReceiverBankId(
            Long senderBankId,
            Long receiverBankId);

    Long countBySenderBankIdOrReceiverBankIdAndCreatedAtBetween(
            Long senderBankId,
            Long receiverBankId,
            LocalDateTime start,
            LocalDateTime end);

    Optional<Transaction>
    findTopBySenderBankIdOrReceiverBankIdOrderByCreatedAtDesc(
            Long senderBankId,
            Long receiverBankId);

    List<Transaction>
    findTop10BySenderBankIdOrReceiverBankIdOrderByCreatedAtDesc(
            Long senderBankId,
            Long receiverBankId);

    @Query("""
           SELECT COALESCE(SUM(t.amount),0)
           FROM Transaction t
           WHERE t.receiverBankId = :bankId
         """)
    BigDecimal getTotalCreditByBankId(
            @Param("bankId")
            Long bankId);

    @Query("""
            SELECT COALESCE(SUM(t.amount),0)
            FROM Transaction t
           WHERE t.senderBankId = :bankId
          """)
    BigDecimal getTotalDebitByBankId(
            @Param("bankId")
            Long bankId);



}

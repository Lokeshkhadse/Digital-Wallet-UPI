package com.example.Query_Service.service;

import com.example.Query_Service.dto.*;
import com.example.Query_Service.entity.Transaction;
import com.example.Query_Service.entity.UserBankBalance;
import com.example.Query_Service.entity.UserBankDetails;
import com.example.Query_Service.exception.ResourceNotFoundException;
import com.example.Query_Service.mapper.TransactionMapper;
import com.example.Query_Service.repository.TransactionRepository;
import com.example.Query_Service.repository.UserBankBalanceRepository;
import com.example.Query_Service.repository.UserBankDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserBankDetailsRepository bankRepository;
    private final UserBankBalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Qualifier("queryExecutor")
    private final Executor queryExecutor;


//    public DashboardHomeResponse getUserDashboard(Long userId) {
//
//        List<UserBankDetails> banks =
//                bankRepository.findByUserId(userId);
//
//        if (banks.isEmpty()) {
//            throw new ResourceNotFoundException(
//                    "No bank accounts found");
//        }
//
//        List<AccountSummaryResponse> accounts =
//                banks.stream()
//                        .map(bank -> {
//
//                            UserBankBalance balance =
//                                    balanceRepository
//                                            .findByUserBankId(bank.getId())
//                                            .orElse(
//                                                    UserBankBalance.builder()
//                                                            .availableBalance(BigDecimal.ZERO)
//                                                            .build());
//
//                            return AccountSummaryResponse.builder()
//                                    .bankId(bank.getId())
//                                    .bankName(bank.getBankName())
//                                    .accountNumber(bank.getAccountNumber())
//                                    .upiId(bank.getUpiId())
//                                    .balance(balance.getAvailableBalance())
//                                    .build();
//                        })
//                        .toList();
//
//        BigDecimal totalBalance =
//                accounts.stream()
//                        .map(AccountSummaryResponse::getBalance)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        return DashboardHomeResponse.builder()
//                .userId(userId)
//                .totalBalance(totalBalance)
//                .linkedAccounts(accounts)
//                .build();
//    }

    public DashboardHomeResponse getUserDashboard(Long userId) {

        List<UserBankDetails> banks =
                bankRepository.findByUserId(userId);

        if (banks.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No bank accounts found");
        }

        List<CompletableFuture<AccountSummaryResponse>> futures =
                banks.stream()
                        .map(bank ->
                                CompletableFuture
                                        .supplyAsync(() -> {

                                            UserBankBalance balance =
                                                    balanceRepository
                                                            .findByUserBankId(bank.getId())
                                                            .orElse(
                                                                    UserBankBalance.builder()
                                                                            .availableBalance(BigDecimal.ZERO)
                                                                            .build());

                                            return AccountSummaryResponse.builder()
                                                    .bankId(bank.getId())
                                                    .bankName(bank.getBankName())
                                                    .accountNumber(bank.getAccountNumber())
                                                    .upiId(bank.getUpiId())
                                                    .balance(balance.getAvailableBalance())
                                                    .build();

                                        }, queryExecutor)

                                        .exceptionally(ex -> {

                                            System.err.println(
                                                    "Balance fetch failed for Bank Id : "
                                                            + bank.getId());

                                            return AccountSummaryResponse.builder()
                                                    .bankId(bank.getId())
                                                    .bankName(bank.getBankName())
                                                    .accountNumber(bank.getAccountNumber())
                                                    .upiId(bank.getUpiId())
                                                    .balance(BigDecimal.ZERO)
                                                    .build();

                                        })

                        )

                        .toList();

        CompletableFuture
                .allOf(
                        futures.toArray(new CompletableFuture[0]))
                .join();        // here it store in future

        List<AccountSummaryResponse> accounts =    // here converting future to Particular Object
                futures.stream()
                        .map(CompletableFuture::join)
                        .toList();

        BigDecimal totalBalance =
                accounts.stream()
                        .map(AccountSummaryResponse::getBalance)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add);

        return DashboardHomeResponse.builder()
                .userId(userId)
                .totalBalance(totalBalance)
                .linkedAccounts(accounts)
                .build();
    }

//    public AccountDashboardResponse getAccountDashboard(Long bankId) {
//
//        UserBankDetails bank =
//                bankRepository.findById(bankId)
//                        .orElseThrow(() ->
//                                new ResourceNotFoundException(
//                                        "Bank account not found"));
//
//        UserBankBalance balance =
//                balanceRepository.findByUserBankId(bankId)
//                        .orElseThrow(() ->
//                                new ResourceNotFoundException(
//                                        "Balance not found"));
//
//        Long totalTransactions =
//                transactionRepository
//                        .countBySenderBankIdOrReceiverBankId(
//                                bankId,
//                                bankId);
//
//        LocalDateTime start =
//                LocalDate.now()
//                        .atStartOfDay();
//
//        LocalDateTime end =
//                LocalDate.now()
//                        .atTime(LocalTime.MAX);
//
//        Long todayTransactions =
//                transactionRepository
//                        .countBySenderBankIdOrReceiverBankIdAndCreatedAtBetween(
//                                bankId,
//                                bankId,
//                                start,
//                                end);
//
//        BigDecimal totalCredit =
//                transactionRepository
//                        .getTotalCreditByBankId(bankId);
//
//        BigDecimal totalDebit =
//                transactionRepository
//                        .getTotalDebitByBankId(bankId);
//
//        Transaction latest =
//                transactionRepository
//                        .findTopBySenderBankIdOrReceiverBankIdOrderByCreatedAtDesc(
//                                bankId,
//                                bankId)
//                        .orElse(null);
//
//        TransferTransactionResponse lastTransaction = null;
//
//        if (latest != null) {
//            lastTransaction =
//                    transactionMapper.toResponse(latest);
//        }
//
//        List<TransferTransactionResponse> recentTransactions =
//                transactionRepository
//                        .findTop10BySenderBankIdOrReceiverBankIdOrderByCreatedAtDesc(
//                                bankId,
//                                bankId)
//                        .stream()
//                        .map(transactionMapper::toResponse)
//                        .toList();
//
//        return AccountDashboardResponse.builder()
//                .bankId(bank.getId())
//                .bankName(bank.getBankName())
//                .accountNumber(bank.getAccountNumber())
//                .upiId(bank.getUpiId())
//                .balance(balance.getAvailableBalance())
//                .totalTransactions(totalTransactions)
//                .todayTransactions(todayTransactions)
//                .totalCredit(totalCredit)
//                .totalDebit(totalDebit)
//                .lastTransaction(lastTransaction)
//                .recentTransactions(recentTransactions)
//                .build();
//    }

    public AccountDashboardResponse getAccountDashboard(Long bankId) {

        CompletableFuture<UserBankDetails> bankFuture =
                CompletableFuture
                        .supplyAsync(() ->
                                        bankRepository.findById(bankId)
                                                .orElseThrow(() ->
                                                        new ResourceNotFoundException(
                                                                "Bank account not found")),
                                queryExecutor)
                        .exceptionally(ex -> {
                            throw new ResourceNotFoundException(
                                    "Unable to fetch Bank Details");
                        });

        CompletableFuture<UserBankBalance> balanceFuture =
                CompletableFuture
                        .supplyAsync(() ->
                                        balanceRepository.findByUserBankId(bankId)
                                                .orElseThrow(() ->
                                                        new ResourceNotFoundException(
                                                                "Balance not found")),
                                queryExecutor)
                        .exceptionally(ex -> {
                            throw new ResourceNotFoundException(
                                    "Unable to fetch Balance");
                        });

        CompletableFuture<Long> totalTransactionFuture =
                CompletableFuture
                        .supplyAsync(() ->
                                        transactionRepository
                                                .countBySenderBankIdOrReceiverBankId(
                                                        bankId,
                                                        bankId),
                                queryExecutor)
                        .exceptionally(ex -> 0L);

        CompletableFuture<Long> todayTransactionFuture =
                CompletableFuture
                        .supplyAsync(() -> {
                            LocalDateTime start =
                                    LocalDate.now()
                                            .atStartOfDay();
                            LocalDateTime end =
                                    LocalDate.now()
                                            .atTime(LocalTime.MAX);

                            return transactionRepository
                                    .countBySenderBankIdOrReceiverBankIdAndCreatedAtBetween(
                                            bankId,
                                            bankId,
                                            start,
                                            end);

                        }, queryExecutor)
                        .exceptionally(ex -> 0L);

        CompletableFuture<BigDecimal> totalCreditFuture =
                CompletableFuture
                        .supplyAsync(() ->
                                        transactionRepository
                                                .getTotalCreditByBankId(bankId),
                                queryExecutor)
                        .exceptionally(ex -> BigDecimal.ZERO);

        CompletableFuture<BigDecimal> totalDebitFuture =
                CompletableFuture
                        .supplyAsync(() ->
                                        transactionRepository
                                                .getTotalDebitByBankId(bankId),
                                queryExecutor)
                        .exceptionally(ex -> BigDecimal.ZERO);


        CompletableFuture<TransferTransactionResponse> latestTransactionFuture =
                CompletableFuture
                        .supplyAsync(() ->
                                        transactionRepository
                                                .findTopBySenderBankIdOrReceiverBankIdOrderByCreatedAtDesc(
                                                        bankId,
                                                        bankId)
                                                .map(transactionMapper::toResponse)
                                                .orElse(null),
                                queryExecutor)
                        .exceptionally(ex -> null);


        CompletableFuture<List<TransferTransactionResponse>> recentTransactionsFuture =
                CompletableFuture
                        .supplyAsync(() ->
                                        transactionRepository
                                                .findTop10BySenderBankIdOrReceiverBankIdOrderByCreatedAtDesc(
                                                        bankId,
                                                        bankId)
                                                .stream()
                                                .map(transactionMapper::toResponse)
                                                .toList(),
                                queryExecutor)
                        .exceptionally(ex -> List.of());


        CompletableFuture
                .allOf(
                        bankFuture,
                        balanceFuture,
                        totalTransactionFuture,
                        todayTransactionFuture,
                        totalCreditFuture,
                        totalDebitFuture,
                        latestTransactionFuture,
                        recentTransactionsFuture
                )
                .join();


        UserBankDetails bank =
                bankFuture.join();

        UserBankBalance balance =
                balanceFuture.join();

        return AccountDashboardResponse.builder()
                .bankId(bank.getId())
                .bankName(bank.getBankName())
                .accountNumber(bank.getAccountNumber())
                .upiId(bank.getUpiId())
                .balance(balance.getAvailableBalance())
                .totalTransactions(totalTransactionFuture.join())
                .todayTransactions(todayTransactionFuture.join())
                .totalCredit(totalCreditFuture.join())
                .totalDebit(totalDebitFuture.join())
                .lastTransaction(latestTransactionFuture.join())
                .recentTransactions(recentTransactionsFuture.join())
                .build();
    }
}
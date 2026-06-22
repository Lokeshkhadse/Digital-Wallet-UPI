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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserBankDetailsRepository bankRepository;
    private final UserBankBalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public DashboardHomeResponse getUserDashboard(Long userId) {

        List<UserBankDetails> banks =
                bankRepository.findByUserId(userId);

        if (banks.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No bank accounts found");
        }

        List<AccountSummaryResponse> accounts =
                banks.stream()
                        .map(bank -> {

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
                        })
                        .toList();

        BigDecimal totalBalance =
                accounts.stream()
                        .map(AccountSummaryResponse::getBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardHomeResponse.builder()
                .userId(userId)
                .totalBalance(totalBalance)
                .linkedAccounts(accounts)
                .build();
    }

    public AccountDashboardResponse getAccountDashboard(Long bankId) {

        UserBankDetails bank =
                bankRepository.findById(bankId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Bank account not found"));

        UserBankBalance balance =
                balanceRepository.findByUserBankId(bankId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Balance not found"));

        Long totalTransactions =
                transactionRepository
                        .countBySenderBankIdOrReceiverBankId(
                                bankId,
                                bankId);

        LocalDateTime start =
                LocalDate.now()
                        .atStartOfDay();

        LocalDateTime end =
                LocalDate.now()
                        .atTime(LocalTime.MAX);

        Long todayTransactions =
                transactionRepository
                        .countBySenderBankIdOrReceiverBankIdAndCreatedAtBetween(
                                bankId,
                                bankId,
                                start,
                                end);

        BigDecimal totalCredit =
                transactionRepository
                        .getTotalCreditByBankId(bankId);

        BigDecimal totalDebit =
                transactionRepository
                        .getTotalDebitByBankId(bankId);

        Transaction latest =
                transactionRepository
                        .findTopBySenderBankIdOrReceiverBankIdOrderByCreatedAtDesc(
                                bankId,
                                bankId)
                        .orElse(null);

        TransferTransactionResponse lastTransaction = null;

        if (latest != null) {
            lastTransaction =
                    transactionMapper.toResponse(latest);
        }

        List<TransferTransactionResponse> recentTransactions =
                transactionRepository
                        .findTop10BySenderBankIdOrReceiverBankIdOrderByCreatedAtDesc(
                                bankId,
                                bankId)
                        .stream()
                        .map(transactionMapper::toResponse)
                        .toList();

        return AccountDashboardResponse.builder()
                .bankId(bank.getId())
                .bankName(bank.getBankName())
                .accountNumber(bank.getAccountNumber())
                .upiId(bank.getUpiId())
                .balance(balance.getAvailableBalance())
                .totalTransactions(totalTransactions)
                .todayTransactions(todayTransactions)
                .totalCredit(totalCredit)
                .totalDebit(totalDebit)
                .lastTransaction(lastTransaction)
                .recentTransactions(recentTransactions)
                .build();
    }
}
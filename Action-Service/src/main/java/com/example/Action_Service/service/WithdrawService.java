package com.example.Action_Service.service;

import com.example.Action_Service.dto.TransferTransactionResponse;
import com.example.Action_Service.dto.UserResponseDto;
import com.example.Action_Service.dto.WithdrawRequest;
import com.example.Action_Service.entity.Transaction;
import com.example.Action_Service.entity.UserBankBalance;
import com.example.Action_Service.entity.UserBankDetails;
import com.example.Action_Service.exception.InsufficientBalanceException;
import com.example.Action_Service.exception.ResourceNotFoundException;
import com.example.Action_Service.exception.ValidationException;
import com.example.Action_Service.feign.AuthServiceClient;
import com.example.Action_Service.mapper.TransactionMapper;
import com.example.Action_Service.rabbitmq.NotificationEvent;
import com.example.Action_Service.repository.BankRepository;
import com.example.Action_Service.repository.TransactionRepository;
import com.example.Action_Service.repository.UserBankBalanceRepository;
import com.example.Action_Service.util.TransactionRefGenerator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawService {

    private final BankRepository bankRepository;
    private final UserBankBalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final AuthServiceClient authClient;
    private final TransactionMapper transactionMapper;
    private final EmailService emailService;
    private final UserUpiPinService upiPinService;
    private final NotificationOutboxService outboxService;

    @Transactional
    public TransferTransactionResponse withdraw(
            WithdrawRequest request) {

        log.info(
                "Withdrawal initiated | UserId={} | BankId={} | Amount={}",
                request.getUserId(),
                request.getUserBankId(),
                request.getAmount());

        // 1. Validate User
        UserResponseDto user =
                authClient.getUserById(
                        request.getUserId());

        if (user == null) {

            log.warn(
                    "User not found | UserId={}",
                    request.getUserId());

            throw new ResourceNotFoundException(
                    "User not found");
        }

        // 2. Validate Bank
        UserBankDetails bank =
                bankRepository.findById(
                                request.getUserBankId())
                        .orElseThrow(() -> {

                            log.warn(
                                    "Bank account not found | BankId={}",
                                    request.getUserBankId());

                            return new ResourceNotFoundException(
                                    "Bank account not found");
                        });

        // 3. Ownership Check
        if (!bank.getUserId()
                .equals(request.getUserId())) {

            log.warn(
                    "User and bank mismatch | UserId={} | BankOwnerId={} | BankId={}",
                    request.getUserId(),
                    bank.getUserId(),
                    request.getUserBankId());

            throw new ValidationException(
                    "User and bank mismatch");
        }

        // Verify PIN (PIN itself is never logged)
        upiPinService.validatePin(
                request.getUserId(),
                request.getUserBankId(),
                request.getUpiPin());

        log.info(
                "UPI PIN validated successfully | UserId={} | BankId={}",
                request.getUserId(),
                request.getUserBankId());

        // 4. Fetch Balance
        UserBankBalance balance =
                balanceRepository.findByUserBankId(
                                request.getUserBankId())
                        .orElseThrow(() -> {

                            log.warn(
                                    "Balance record not found | BankId={}",
                                    request.getUserBankId());

                            return new ResourceNotFoundException(
                                    "Balance not found");
                        });

        // 5. Balance Check
        if (balance.getAvailableBalance()
                .compareTo(request.getAmount()) < 0) {

            log.warn(
                    "Insufficient balance | UserId={} | BankId={} | AvailableBalance={} | RequestedAmount={}",
                    request.getUserId(),
                    request.getUserBankId(),
                    balance.getAvailableBalance(),
                    request.getAmount());

            throw new InsufficientBalanceException(
                    "Available balance is ₹"
                            + balance.getAvailableBalance()
                            + ". Unable to withdraw ₹"
                            + request.getAmount());
        }

        // 6. Deduct Balance
        balance.setAvailableBalance(
                balance.getAvailableBalance()
                        .subtract(request.getAmount()));

        balanceRepository.save(balance);

        log.info(
                "Balance updated after withdrawal | UserId={} | BankId={} | RemainingBalance={}",
                request.getUserId(),
                request.getUserBankId(),
                balance.getAvailableBalance());

        // 7. Create Transaction
        String refNo =
                TransactionRefGenerator.generateRefNo();

        Transaction transaction =
                transactionMapper.toWithdrawEntity(
                        request,
                        refNo);

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        log.info(
                "Withdrawal transaction saved | TransactionRef={} | UserId={} | Amount={}",
                refNo,
                request.getUserId(),
                request.getAmount());

        // Queue email notification
        outboxService.saveNotification(
                NotificationEvent.builder()
                        .to(user.getEmail())
                        .subject("Withdrawal Confirmation | Digital Wallet")
                        .templateName("transaction-mail")
                        .variables(
                                Map.of(
                                        "name", user.getName(),
                                        "amount", request.getAmount(),
                                        "referenceNo", refNo,
                                        "balance", balance.getAvailableBalance(),
                                        "type", "Withdrawal"
                                )
                        )
                        .build()
        );

        log.info(
                "Withdrawal notification queued | TransactionRef={} | Email={}",
                refNo,
                user.getEmail());

        // 9. Response
        log.info(
                "Withdrawal completed successfully | TransactionRef={} | UserId={} | Amount={}",
                refNo,
                request.getUserId(),
                request.getAmount());

        return transactionMapper.toResponse(
                savedTransaction,
                "Withdraw successful");
    }
}
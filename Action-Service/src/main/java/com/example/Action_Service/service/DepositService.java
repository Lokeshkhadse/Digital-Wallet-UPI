package com.example.Action_Service.service;

import com.example.Action_Service.dto.DepositRequest;
import com.example.Action_Service.dto.TransferTransactionResponse;
import com.example.Action_Service.dto.UserResponseDto;
import com.example.Action_Service.entity.Transaction;
import com.example.Action_Service.entity.UserBankBalance;
import com.example.Action_Service.entity.UserBankDetails;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DepositService {

    private final BankRepository bankRepository;
    private final UserBankBalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final AuthServiceClient authClient;
    private final TransactionMapper transactionMapper;
    private final EmailService emailService;
    private final NotificationOutboxService outboxService;

    @Transactional
    public TransferTransactionResponse deposit(
            DepositRequest request) {

        log.info("Deposit request received. UserId={}, BankId={}, Amount={}",
                request.getUserId(),
                request.getUserBankId(),
                request.getAmount());

        log.debug("Fetching user details. UserId={}",
                request.getUserId());

        UserResponseDto user =
                authClient.getUserById(
                        request.getUserId());

        if (user == null) {

            log.error("User not found. UserId={}",
                    request.getUserId());

            throw new ResourceNotFoundException(
                    "User not found");
        }

        log.debug("Fetching bank details. BankId={}",
                request.getUserBankId());

        UserBankDetails bank =
                bankRepository.findById(
                                request.getUserBankId())
                        .orElseThrow(() -> {

                            log.error("Bank account not found. BankId={}",
                                    request.getUserBankId());

                            return new ResourceNotFoundException(
                                    "Bank account not found");
                        });

        if (!bank.getUserId()
                .equals(request.getUserId())) {

            log.warn("User-bank ownership mismatch. UserId={}, BankId={}",
                    request.getUserId(),
                    request.getUserBankId());

            throw new ValidationException(
                    "User and bank mismatch");
        }

        log.debug("Fetching balance details. BankId={}",
                request.getUserBankId());

        UserBankBalance balance =
                balanceRepository.findByUserBankId(
                                request.getUserBankId())
                        .orElseThrow(() -> {

                            log.error("Balance record not found. BankId={}",
                                    request.getUserBankId());

                            return new ResourceNotFoundException(
                                    "Balance record not found");
                        });

        balance.setAvailableBalance(
                balance.getAvailableBalance()
                        .add(request.getAmount()));

        balanceRepository.save(balance);

        log.debug("Balance updated successfully. BankId={}, NewBalance={}",
                request.getUserBankId(),
                balance.getAvailableBalance());

        String refNo =
                TransactionRefGenerator.generateRefNo();

        Transaction transaction =
                transactionMapper.toDepositEntity(
                        request,
                        refNo);

        transactionRepository.save(transaction);

        log.info("Deposit transaction saved successfully. ReferenceNo={}",
                refNo);

        outboxService.saveNotification(
                NotificationEvent.builder()
                        .to(user.getEmail())
                        .subject("Deposit Confirmation | Digital Wallet")
                        .templateName("transaction-mail")
                        .variables(
                                Map.of(
                                        "name", user.getName(),
                                        "amount", request.getAmount(),
                                        "referenceNo", refNo,
                                        "balance", balance.getAvailableBalance(),
                                        "type", "Deposit"
                                )
                        )
                        .build()
        );

        log.info("Deposit notification saved to Outbox. ReferenceNo={}",
                refNo);

        log.info("Deposit completed successfully. UserId={}, ReferenceNo={}",
                request.getUserId(),
                refNo);

        return transactionMapper.toResponse(
                transaction,
                "Deposit successful");
    }
}
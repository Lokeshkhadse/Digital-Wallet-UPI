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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
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

        // 1. Validate User
        UserResponseDto user =
                authClient.getUserById(request.getUserId());

        if (user == null) {
            throw new ResourceNotFoundException(
                    "User not found");
        }

        // 2. Validate Bank
        UserBankDetails bank =
                bankRepository.findById(
                                request.getUserBankId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Bank account not found"));

        // 3. Ownership Check
        if (!bank.getUserId()
                .equals(request.getUserId())) {

            throw new ValidationException(
                    "User and bank mismatch");
        }

        //verify pin
        upiPinService.validatePin(
                request.getUserId(),
                request.getUserBankId(),
                request.getUpiPin());

        // 4. Fetch Balance
        UserBankBalance balance =
                balanceRepository.findByUserBankId(
                                request.getUserBankId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Balance not found"));

        // 5. Balance Check
        if (balance.getAvailableBalance()
                .compareTo(request.getAmount()) < 0) {

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

        // 7. Create Transaction
        String refNo =
                TransactionRefGenerator.generateRefNo();

        Transaction transaction =
                transactionMapper.toWithdrawEntity(
                        request,
                        refNo);

        Transaction savedTransaction =
                transactionRepository.save(transaction);

//        //8. email part
//        notificationProducer.sendNotification(
//                NotificationEvent.builder()
//                        .to(user.getEmail())
//                        .subject("Withdrawal Confirmation | Digital Wallet")
//                        .templateName("transaction-mail")
//                        .variables(
//                                Map.of(
//                                        "name", user.getName(),
//                                        "amount", request.getAmount(),
//                                        "referenceNo", refNo,
//                                        "balance", balance.getAvailableBalance(),
//                                        "type", "Withdrawal"
//                                )
//                        )
//                        .build()
//        );

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



        // 9. Response
        return transactionMapper.toResponse(
                savedTransaction,
                "Withdraw successful");
    }
}
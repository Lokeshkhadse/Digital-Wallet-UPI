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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;


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


         //validate user
        UserResponseDto user =
                authClient.getUserById(
                        request.getUserId());
        System.out.println(user);

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


        // 3. Validate Ownership
        if (!bank.getUserId()
                .equals(request.getUserId())) {

            throw new ValidationException(
                    "User and bank mismatch");
        }


        // 4. Balance Row
        UserBankBalance balance =
                balanceRepository.findByUserBankId(
                                request.getUserBankId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Balance record not found"));


        // 5. Add Amount
        balance.setAvailableBalance(
                balance.getAvailableBalance()
                        .add(request.getAmount()));

        balanceRepository.save(balance);


        // 6. Create Transaction
        String refNo = TransactionRefGenerator.generateRefNo();

        Transaction transaction =
                transactionMapper.toDepositEntity(
                        request,
                        refNo);

        transactionRepository.save(transaction);

        System.out.println("Email = " + user.getEmail());
        System.out.println("Name = " + user.getName());
        System.out.println("Amount = " + request.getAmount());
        System.out.println("RefNo = " + refNo);
        System.out.println("Balance = " + balance.getAvailableBalance());


        //email part


//        notificationProducer.sendNotification(
//                NotificationEvent.builder()
//                        .to(user.getEmail())
//                        .subject("Deposit Confirmation | Digital Wallet")
//                        .templateName("transaction-mail")
//                        .variables(
//                                Map.of(
//                                        "name", user.getName(),
//                                        "amount", request.getAmount(),
//                                        "referenceNo", refNo,
//                                        "balance", balance.getAvailableBalance(),
//                                        "type", "Deposit"
//                                )
//                        )
//                        .build()
//        );

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



        return transactionMapper.toResponse(
                transaction,
                "Deposit successful");
    }
}
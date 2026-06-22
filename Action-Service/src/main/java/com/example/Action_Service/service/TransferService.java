
package com.example.Action_Service.service;

import com.example.Action_Service.dto.*;
import com.example.Action_Service.entity.IdempotencyKey;
import com.example.Action_Service.entity.Transaction;
import com.example.Action_Service.entity.UserBankBalance;
import com.example.Action_Service.enums.TransactionStatus;
import com.example.Action_Service.enums.TransactionType;
import com.example.Action_Service.exception.*;
import com.example.Action_Service.feign.AuthServiceClient;
import com.example.Action_Service.feign.LedgerServiceClient;
import com.example.Action_Service.mapper.TransactionMapper;
import com.example.Action_Service.rabbitmq.NotificationEvent;
import com.example.Action_Service.repository.BeneficiaryRepository;
import com.example.Action_Service.repository.IdempotencyRepository;
import com.example.Action_Service.repository.TransactionRepository;
import com.example.Action_Service.repository.UserBankBalanceRepository;
import com.example.Action_Service.util.TransactionRefGenerator;
import com.example.Action_Service.util.WalletConstants;
import com.example.Action_Service.util.IdempotencyKeyGenerator;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final UserBankBalanceRepository balanceRepo;
    private final TransactionRepository transactionRepo;
    private final IdempotencyRepository idempotencyRepo;
    private final TransactionMapper transactionMapper;
    private final AuthServiceClient authServiceClient;
    private final LedgerServiceClient ledgerServiceClient;
    private final EmailService emailService;
    private final BeneficiaryRepository beneficiaryRepository;
    private final UserUpiPinService upiPinService;
    private final FraudService fraudService;
    private final NotificationOutboxService outboxService;

    @Transactional
    public TransferTransactionResponse transfer(
            TransferTransactionRequest req) {

        log.info(
                "Transfer Started {} -> {} Amount {}",
                req.getSenderBankId(),
                req.getReceiverBankId(),
                req.getAmount());

        // Amount Validation
        if (req.getAmount() == null
                || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAmountException(
                    "Transfer amount must be greater than 0");
        }



        // Same Bank Validation
        if (req.getSenderBankId()
                .equals(req.getReceiverBankId())) {

            throw new ValidationException(
                    "Sender and Receiver bank account cannot be same");
        }

        // Idempotency Validation
        String idempotencyKey =
                IdempotencyKeyGenerator.generate();

        Optional<IdempotencyKey> existingKey =
                idempotencyRepo.findByIdempotencyKey(
                        idempotencyKey);

        if (existingKey.isPresent()) {

            throw new DuplicateResourceException(
                    "This transaction has already been processed");
        }

        // User Validation
        UserResponseDto sender;
        UserResponseDto receiver;

        try {

            sender = authServiceClient.getUserById(
                    req.getSenderUserId());

        } catch (FeignException.NotFound ex) {

            throw new UserNotFoundException(
                    "Sender user not found");

        } catch (FeignException ex) {

            throw new AuthServiceUnavailableException(
                    "Auth Service unavailable");
        }

        try {

            receiver = authServiceClient.getUserById(
                    req.getReceiverUserId());

        } catch (FeignException.NotFound ex) {

            throw new UserNotFoundException(
                    "Receiver user not found");

        } catch (FeignException ex) {

            throw new AuthServiceUnavailableException(
                    "Auth Service unavailable");
        }

        // Beneficiary Validation (Future)
//        boolean beneficiaryExists =
//                beneficiaryRepository
//                        .existsByUserIdAndBeneficiaryUserId(
//                                req.getSenderUserId(),
//                                req.getReceiverUserId());
//
//        if (!beneficiaryExists) {
//            throw new ValidationException(
//                    "Receiver is not added as beneficiary");
//        }

        // Fetch Balances
        UserBankBalance senderBalance =
                balanceRepo.lockByUserBankId(
                                req.getSenderBankId())
                        .orElseThrow(() ->
                                new BankNotFoundException(
                                        "Sender balance not found"));

        UserBankBalance receiverBalance =
                balanceRepo.lockByUserBankId(
                                req.getReceiverBankId())
                        .orElseThrow(() ->
                                new BankNotFoundException(
                                        "Receiver balance not found"));

        // Bank Ownership Validation
        if (!senderBalance.getUserId()
                .equals(req.getSenderUserId())) {

            throw new InvalidBankOwnerException(
                    "Sender bank does not belong to sender user");
        }

        if (!receiverBalance.getUserId()
                .equals(req.getReceiverUserId())) {

            throw new InvalidBankOwnerException(
                    "Receiver bank does not belong to receiver user");
        }

        // UPI PIN Validation
        upiPinService.validatePin(
                req.getSenderUserId(),
                req.getSenderBankId(),
                req.getUpiPin());

        //fraud detection
        fraudService.fraudCheck(
                req.getSenderUserId(),
                req.getSenderBankId(),
                req.getAmount());

        // Daily Limit
        LocalDateTime startOfDay =
                LocalDateTime.now()
                        .toLocalDate()
                        .atStartOfDay();

        LocalDateTime endOfDay =
                startOfDay.plusDays(1);

        BigDecimal todayTransferredAmount =
                transactionRepo.sumTodayTransferAmount(
                        req.getSenderBankId(),
                        TransactionType.TRANSFER,
                        TransactionStatus.SUCCESS,
                        startOfDay,
                        endOfDay);

        if (todayTransferredAmount == null) {
            todayTransferredAmount = BigDecimal.ZERO;
        }

        BigDecimal totalAfterTransfer =
                todayTransferredAmount.add(
                        req.getAmount());

        if (totalAfterTransfer.compareTo(
                WalletConstants.DAILY_TRANSFER_LIMIT) > 0) {

            BigDecimal remaining =
                    WalletConstants.DAILY_TRANSFER_LIMIT
                            .subtract(todayTransferredAmount);

            throw new DailyLimitExceededException(
                    "Daily transfer limit exceeded. Remaining limit today is ₹"
                            + remaining);
        }



        // Balance Check
        if (senderBalance.getAvailableBalance()
                .compareTo(req.getAmount()) < 0) {

            throw new InsufficientBalanceException(
                    "Available balance is ₹"
                            + senderBalance.getAvailableBalance()
                            + ". Unable to transfer ₹"
                            + req.getAmount());
        }

        // Create Transaction
        String refNo =
                TransactionRefGenerator.generateRefNo();

        Transaction tx =
                transactionMapper.toEntity(
                        req,
                        refNo);

        tx.setTransactionStatus(
                TransactionStatus.PENDING);

        transactionRepo.save(tx);

        BigDecimal senderBefore =
                senderBalance.getAvailableBalance();

        BigDecimal receiverBefore =
                receiverBalance.getAvailableBalance();

        // Update Balances
        senderBalance.setAvailableBalance(
                senderBalance.getAvailableBalance()
                        .subtract(req.getAmount()));

        receiverBalance.setAvailableBalance(
                receiverBalance.getAvailableBalance()
                        .add(req.getAmount()));

        balanceRepo.save(senderBalance);
        balanceRepo.save(receiverBalance);

        try {

            // Debit Ledger
            LedgerRequest debitEntry =
                    LedgerRequest.builder()
                            .transactionRefNo(refNo)
                            .userId(req.getSenderUserId())
                            .userBankId(req.getSenderBankId())
                            .entryType("DEBIT")
                            .amount(req.getAmount())
                            .balanceBefore(senderBefore)
                            .balanceAfter(
                                    senderBalance.getAvailableBalance())
                            .remarks("Transfer Debit")
                            .build();

            ledgerServiceClient.createLedgerEntry(
                    debitEntry);

            // Credit Ledger
            LedgerRequest creditEntry =
                    LedgerRequest.builder()
                            .transactionRefNo(refNo)
                            .userId(req.getReceiverUserId())
                            .userBankId(req.getReceiverBankId())
                            .entryType("CREDIT")
                            .amount(req.getAmount())
                            .balanceBefore(receiverBefore)
                            .balanceAfter(
                                    receiverBalance.getAvailableBalance())
                            .remarks("Transfer Credit")
                            .build();

            ledgerServiceClient.createLedgerEntry(
                    creditEntry);

            tx.setTransactionStatus(
                    TransactionStatus.SUCCESS);

            transactionRepo.save(tx);

        } catch (Exception ex) {

            log.error("Ledger service call failed", ex);

            tx.setTransactionStatus(
                    TransactionStatus.FAILED);

            transactionRepo.save(tx);

            throw new LedgerServiceUnavailableException(
                    "Ledger Service unavailable or check the roles");
        }

        // Save Idempotency
        IdempotencyKey key =
                new IdempotencyKey();

        key.setIdempotencyKey(idempotencyKey);

        key.setTransactionRefNo(refNo);

        key.setCreatedAt(
                LocalDateTime.now());

        idempotencyRepo.save(key);

        // Email (Never Fail Transaction)
        try {

//            notificationProducer.sendNotification(
//                    NotificationEvent.builder()
//                            .to(sender.getEmail())
//                            .subject("Fund Transfer Successful | Digital Wallet")
//                            .templateName("transfer-sender")
//                            .variables(Map.of(
//                                    "name", sender.getName(),
//                                    "amount", req.getAmount(),
//                                    "referenceNo", refNo)
//                            )
//                            .build()
//            );

            outboxService.saveNotification(
                    NotificationEvent.builder()
                            .to(sender.getEmail())
                            .subject("Fund Transfer Successful | Digital Wallet")
                            .templateName("transfer-sender")
                            .variables(Map.of(
                                    "name", sender.getName(),
                                    "amount", req.getAmount(),
                                    "referenceNo", refNo)
                            )
                            .build()
            );



//            notificationProducer.sendNotification(
//                    NotificationEvent.builder()
//                            .to(receiver.getEmail())
//                            .subject("Funds Received Successfully  | Digital Wallet")
//                            .templateName("transfer-receiver")
//                            .variables(Map.of(
//                                    "name", receiver.getName(),
//                                    "amount", req.getAmount(),
//                                    "referenceNo", refNo))
//                            .build()
//            );

            outboxService.saveNotification(
                    NotificationEvent.builder()
                            .to(receiver.getEmail())
                            .subject("Funds Received Successfully  | Digital Wallet")
                            .templateName("transfer-receiver")
                            .variables(Map.of(
                                    "name", receiver.getName(),
                                    "amount", req.getAmount(),
                                    "referenceNo", refNo))
                            .build()
            );

        } catch (Exception ex) {

            log.error(
                    "Email sending failed for RefNo {}",
                    refNo,
                    ex);
        }

        log.info(
                "Transfer Success RefNo {}",
                refNo);

        return transactionMapper.toResponse(
                tx,
                "Transfer successful");
    }


}

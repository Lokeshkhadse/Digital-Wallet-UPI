package com.example.Action_Service.service;

import com.example.Action_Service.dto.LedgerRequest;
import com.example.Action_Service.dto.TransferTransactionRequest;
import com.example.Action_Service.dto.TransferTransactionResponse;
import com.example.Action_Service.dto.UserResponseDto;
import com.example.Action_Service.entity.IdempotencyKey;
import com.example.Action_Service.entity.Transaction;
import com.example.Action_Service.entity.UserBankBalance;
import com.example.Action_Service.enums.TransactionStatus;
import com.example.Action_Service.exception.*;
import com.example.Action_Service.feign.AuthServiceClient;
import com.example.Action_Service.feign.LedgerServiceClient;
import com.example.Action_Service.mapper.TransactionMapper;
import com.example.Action_Service.repository.IdempotencyRepository;
import com.example.Action_Service.repository.TransactionRepository;
import com.example.Action_Service.repository.UserBankBalanceRepository;
import com.example.Action_Service.util.TransactionRefGenerator;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final UserBankBalanceRepository balanceRepo;
    private final TransactionRepository transactionRepo;
    private final IdempotencyRepository idempotencyRepo;
    private final TransactionMapper transactionMapper;
    private final AuthServiceClient authServiceClient;
    private final LedgerServiceClient ledgerServiceClient;

//    @Transactional
//    public TransferTransactionResponse transfer(TransferTransactionRequest req) {
//
//        // 1 Check Idempotency
//        Optional<IdempotencyKey> existingKey =
//                idempotencyRepo.findByIdempotencyKey(req.getIdempotencyKey());
//
//        if (existingKey.isPresent()) {
//            throw new DuplicateResourceException(
//                    " This transaction has already been processed");
//
//        }
//
//        // 2 Validate sender/receiver users
//        if (req.getSenderUserId().equals(req.getReceiverUserId())) {
//            throw new SameSenderReceiverException("Sender and receiver cannot be same");
//        }
//
//        UserResponseDto sender;
//        UserResponseDto receiver;
//
//        try {
//            sender = authServiceClient.getUserById(req.getSenderUserId());
//        } catch (FeignException.NotFound e) {
//            throw new UserNotFoundException("Sender user not found");
//        }
//
//        try {
//            receiver = authServiceClient.getUserById(req.getReceiverUserId());
//        } catch (FeignException.NotFound e) {
//            throw new UserNotFoundException("Receiver user not found");
//        }
//
//        // 3 Fetch balances
////        UserBankBalance senderBalance = balanceRepo.findByUserBankId(req.getSenderBankId())
////                .orElseThrow(() -> new BankNotFoundException("Sender balance not found"));
//
//            UserBankBalance senderBalance =
//                    balanceRepo.lockByUserBankId(req.getSenderBankId())
//                            .orElseThrow(() -> new BankNotFoundException("Sender balance not found"));
//
////        UserBankBalance receiverBalance = balanceRepo.findByUserBankId(req.getReceiverBankId())
////                .orElseThrow(() -> new BankNotFoundException("Receiver balance not found"));
//
//        UserBankBalance receiverBalance = balanceRepo.lockByUserBankId(req.getReceiverBankId())
//                .orElseThrow(() -> new BankNotFoundException("Receiver balance not found"));
//
//        // 4 Check sufficient balance
//        if (senderBalance.getAvailableBalance().compareTo(req.getAmount()) < 0) {
//            throw new InsufficientBalanceException(
//                    "Insufficient balance. Available balance is ₹"
//                            + senderBalance.getAvailableBalance()
//                            + ", but transfer amount is ₹"
//                            + req.getAmount()
//            );
//        }
//
//        // 5 Create Transaction record (initially PENDING)
//        String refNo = TransactionRefGenerator.generateRefNo();
//        Transaction tx = transactionMapper.toEntity(req, refNo);
//        transactionRepo.save(tx);
//
//        //6 calculate before and after balances for ledger entries
//        BigDecimal senderBefore = senderBalance.getAvailableBalance();
//        BigDecimal receiverBefore = receiverBalance.getAvailableBalance();
//
//        // 7 Update balances
//        senderBalance.setAvailableBalance(
//                senderBalance.getAvailableBalance().subtract(req.getAmount()));
//
//        receiverBalance.setAvailableBalance(
//                receiverBalance.getAvailableBalance().add(req.getAmount()));
//
//        balanceRepo.save(senderBalance);
//        balanceRepo.save(receiverBalance);
//
//        // 8. Mark transaction success
//        tx.setTransactionStatus(TransactionStatus.SUCCESS);
//        transactionRepo.save(tx);
//
//
//        // 9. LEDGER ENTRY - DEBIT (SENDER)
//        LedgerRequest debitEntry = LedgerRequest.builder()
//                .transactionRefNo(refNo)
//                .userId(req.getSenderUserId())
//                .userBankId(req.getSenderBankId())
//                .entryType("DEBIT")
//                .amount(req.getAmount())
//                .balanceBefore(senderBefore)
//                .balanceAfter(senderBalance.getAvailableBalance())
//                .remarks("Transfer Debit")
//                .build();
//
//        ledgerServiceClient.createLedgerEntry(debitEntry);
//
//        // 10. LEDGER ENTRY - CREDIT (RECEIVER)
//        LedgerRequest creditEntry = LedgerRequest.builder()
//                .transactionRefNo(refNo)
//                .userId(req.getReceiverUserId())
//                .userBankId(req.getReceiverBankId())
//                .entryType("CREDIT")
//                .amount(req.getAmount())
//                .balanceBefore(receiverBefore)
//                .balanceAfter(receiverBalance.getAvailableBalance())
//                .remarks("Transfer Credit")
//                .build();
//
//        ledgerServiceClient.createLedgerEntry(creditEntry);
//
//
//        // 11 Save idempotency
//        IdempotencyKey key = new IdempotencyKey();
//        key.setIdempotencyKey(req.getIdempotencyKey());
//        key.setTransactionRefNo(refNo);
//        key.setCreatedAt(LocalDateTime.now());
//        idempotencyRepo.save(key);
//
//
//        return transactionMapper.toResponse(tx,  "Transfer successful");
//    }


@Transactional
public TransferTransactionResponse transfer(TransferTransactionRequest req) {

    // 0. Amount validation
    if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new InvalidAmountException("Transfer amount must be greater than 0");
    }

    // 1 Check Idempotency
    Optional<IdempotencyKey> existingKey =
            idempotencyRepo.findByIdempotencyKey(req.getIdempotencyKey());

    if (existingKey.isPresent()) {
        throw new DuplicateResourceException(
                " This transaction has already been processed");
    }

    // 2 Validate sender/receiver users
    if (req.getSenderUserId().equals(req.getReceiverUserId())) {
        throw new SameSenderReceiverException("Sender and receiver cannot be same");
    }

    UserResponseDto sender;
    UserResponseDto receiver;

    try {
        sender = authServiceClient.getUserById(req.getSenderUserId());
    } catch (FeignException.NotFound e) {
        throw new UserNotFoundException("Sender user not found");
    }

    try {
        receiver = authServiceClient.getUserById(req.getReceiverUserId());
    } catch (FeignException.NotFound e) {
        throw new UserNotFoundException("Receiver user not found");
    }

    // 3 Fetch balances
    UserBankBalance senderBalance =
            balanceRepo.lockByUserBankId(req.getSenderBankId())
                    .orElseThrow(() -> new BankNotFoundException("Sender balance not found"));

    UserBankBalance receiverBalance =
            balanceRepo.lockByUserBankId(req.getReceiverBankId())
                    .orElseThrow(() -> new BankNotFoundException("Receiver balance not found"));

    // 4 Bank ownership validation (IMPORTANT EDGE CASE FIX)

    if (!senderBalance.getUserId().equals(req.getSenderUserId())) {
        throw new InvalidBankOwnerException("Sender bank does not belong to sender user");
    }

    if (!receiverBalance.getUserId().equals(req.getReceiverUserId())) {
        throw new InvalidBankOwnerException("Receiver bank does not belong to receiver user");
    }

    // 5 Check sufficient balance
    if (senderBalance.getAvailableBalance().compareTo(req.getAmount()) < 0) {
        throw new InsufficientBalanceException(
                "Insufficient balance. Available balance is ₹"
                        + senderBalance.getAvailableBalance()
                        + ", but transfer amount is ₹"
                        + req.getAmount()
        );
    }

    // 6 Create Transaction record (initially PENDING)
    String refNo = TransactionRefGenerator.generateRefNo();
    Transaction tx = transactionMapper.toEntity(req, refNo);
    transactionRepo.save(tx);

    // 7 calculate before and after balances for ledger entries
    BigDecimal senderBefore = senderBalance.getAvailableBalance();
    BigDecimal receiverBefore = receiverBalance.getAvailableBalance();

    // 8 Update balances
    senderBalance.setAvailableBalance(
            senderBalance.getAvailableBalance().subtract(req.getAmount()));

    receiverBalance.setAvailableBalance(
            receiverBalance.getAvailableBalance().add(req.getAmount()));

    balanceRepo.save(senderBalance);
    balanceRepo.save(receiverBalance);

    // 9 Mark transaction success
    tx.setTransactionStatus(TransactionStatus.SUCCESS);
    transactionRepo.save(tx);

    // 10 LEDGER ENTRY - DEBIT (SENDER)
    LedgerRequest debitEntry = LedgerRequest.builder()
            .transactionRefNo(refNo)
            .userId(req.getSenderUserId())
            .userBankId(req.getSenderBankId())
            .entryType("DEBIT")
            .amount(req.getAmount())
            .balanceBefore(senderBefore)
            .balanceAfter(senderBalance.getAvailableBalance())
            .remarks("Transfer Debit")
            .build();

    ledgerServiceClient.createLedgerEntry(debitEntry);

    // 11 LEDGER ENTRY - CREDIT (RECEIVER)
    LedgerRequest creditEntry = LedgerRequest.builder()
            .transactionRefNo(refNo)
            .userId(req.getReceiverUserId())
            .userBankId(req.getReceiverBankId())
            .entryType("CREDIT")
            .amount(req.getAmount())
            .balanceBefore(receiverBefore)
            .balanceAfter(receiverBalance.getAvailableBalance())
            .remarks("Transfer Credit")
            .build();

    ledgerServiceClient.createLedgerEntry(creditEntry);

    // 12 Save idempotency
    IdempotencyKey key = new IdempotencyKey();
    key.setIdempotencyKey(req.getIdempotencyKey());
    key.setTransactionRefNo(refNo);
    key.setCreatedAt(LocalDateTime.now());
    idempotencyRepo.save(key);

    return transactionMapper.toResponse(tx, "Transfer successful");
}


}


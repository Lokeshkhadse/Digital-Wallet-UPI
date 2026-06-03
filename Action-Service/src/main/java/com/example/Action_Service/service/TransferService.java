package com.example.Action_Service.service;

import com.example.Action_Service.dto.LedgerRequest;
import com.example.Action_Service.dto.TransferTransactionRequest;
import com.example.Action_Service.dto.TransferTransactionResponse;
import com.example.Action_Service.entity.IdempotencyKey;
import com.example.Action_Service.entity.Transaction;
import com.example.Action_Service.entity.UserBankBalance;
import com.example.Action_Service.enums.TransactionStatus;
import com.example.Action_Service.exception.BankNotFoundException;
import com.example.Action_Service.exception.DuplicateResourceException;
import com.example.Action_Service.exception.InsufficientBalanceException;
import com.example.Action_Service.exception.SameSenderReceiverException;
import com.example.Action_Service.feign.AuthServiceClient;
import com.example.Action_Service.feign.LedgerServiceClient;
import com.example.Action_Service.mapper.TransactionMapper;
import com.example.Action_Service.repository.IdempotencyRepository;
import com.example.Action_Service.repository.TransactionRepository;
import com.example.Action_Service.repository.UserBankBalanceRepository;
import com.example.Action_Service.util.TransactionRefGenerator;
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

    @Transactional
    public TransferTransactionResponse transfer(TransferTransactionRequest req) {

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

        authServiceClient.getUserById(req.getSenderUserId());
        authServiceClient.getUserById(req.getReceiverUserId());

        // 3 Fetch balances
//        UserBankBalance senderBalance = balanceRepo.findByUserBankId(req.getSenderBankId())
//                .orElseThrow(() -> new BankNotFoundException("Sender balance not found"));

            UserBankBalance senderBalance =
                    balanceRepo.lockByUserBankId(req.getSenderBankId())
                            .orElseThrow(() -> new BankNotFoundException("Sender balance not found"));

//        UserBankBalance receiverBalance = balanceRepo.findByUserBankId(req.getReceiverBankId())
//                .orElseThrow(() -> new BankNotFoundException("Receiver balance not found"));

        UserBankBalance receiverBalance = balanceRepo.lockByUserBankId(req.getReceiverBankId())
                .orElseThrow(() -> new BankNotFoundException("Receiver balance not found"));

        // 4 Check sufficient balance
        if (senderBalance.getAvailableBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available balance is ₹"
                            + senderBalance.getAvailableBalance()
                            + ", but transfer amount is ₹"
                            + req.getAmount()
            );
        }

        // 5 Create Transaction record (initially PENDING)
        String refNo = TransactionRefGenerator.generateRefNo();
        Transaction tx = transactionMapper.toEntity(req, refNo);
        transactionRepo.save(tx);

        //6 calculate before and after balances for ledger entries
        BigDecimal senderBefore = senderBalance.getAvailableBalance();
        BigDecimal receiverBefore = receiverBalance.getAvailableBalance();

        // 7 Update balances
        senderBalance.setAvailableBalance(
                senderBalance.getAvailableBalance().subtract(req.getAmount()));

        receiverBalance.setAvailableBalance(
                receiverBalance.getAvailableBalance().add(req.getAmount()));

        balanceRepo.save(senderBalance);
        balanceRepo.save(receiverBalance);

        // 8. Mark transaction success
        tx.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionRepo.save(tx);


        // 9. LEDGER ENTRY - DEBIT (SENDER)
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

        // 10. LEDGER ENTRY - CREDIT (RECEIVER)
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


        // 11 Save idempotency
        IdempotencyKey key = new IdempotencyKey();
        key.setIdempotencyKey(req.getIdempotencyKey());
        key.setTransactionRefNo(refNo);
        key.setCreatedAt(LocalDateTime.now());
        idempotencyRepo.save(key);


        return transactionMapper.toResponse(tx,  "Transfer successful");
    }
}


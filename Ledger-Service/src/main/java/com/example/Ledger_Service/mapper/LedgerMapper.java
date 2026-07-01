package com.example.Ledger_Service.mapper;

import com.example.Ledger_Service.dto.LedgerRequest;
import com.example.Ledger_Service.dto.LedgerResponse;
import com.example.Ledger_Service.entity.LedgerTransaction;
import com.example.Ledger_Service.enums.LedgerEntryType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LedgerMapper {

    //request to entity
    public LedgerTransaction toEntity(LedgerRequest ledgerRequest){
        LedgerTransaction ledgerTransaction = LedgerTransaction.builder().
                                              transactionRefNo(ledgerRequest.getTransactionRefNo()).
                                              userId(ledgerRequest.getUserId()).
                                              userBankId(ledgerRequest.getUserBankId()).
                                              entryType(LedgerEntryType.valueOf(ledgerRequest.getEntryType())).
                                              amount(ledgerRequest.getAmount()).
                                              balanceBefore(ledgerRequest.getBalanceBefore()).
                                              balanceAfter(ledgerRequest.getBalanceAfter()).
                                              remarks(ledgerRequest.getRemarks()).
                                              createdAt(LocalDateTime.now()).
                                              build();
        return ledgerTransaction;
    }

    //entity to response
    public LedgerResponse toResponse(LedgerTransaction ledgerTransaction){
        LedgerResponse ledgerResponse = LedgerResponse.builder().
                                              ledgerId(ledgerTransaction.getLedgerId()).
                                              transactionRefNo(ledgerTransaction.getTransactionRefNo()).
                                              userId(ledgerTransaction.getUserId()).
                                              entryType(ledgerTransaction.getEntryType().name()).
                                              amount(ledgerTransaction.getAmount()).
                                              balanceBefore(ledgerTransaction.getBalanceBefore()).
                                              balanceAfter(ledgerTransaction.getBalanceAfter()).
                                              build();
        return ledgerResponse;
    }
}


package com.example.Query_Service.service;

import com.example.Query_Service.dto.UserBankDetailsResponse;
import com.example.Query_Service.entity.UserBankDetails;
import com.example.Query_Service.enums.AccountType;
import com.example.Query_Service.exception.BankNotFoundException;
import com.example.Query_Service.mapper.UserBankDetailsMapper;
import com.example.Query_Service.repository.UserBankDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBankDetailsService {

    private final UserBankDetailsRepository repository;
    private final UserBankDetailsMapper mapper;

    public List<UserBankDetailsResponse> getBanksByUserId(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public UserBankDetailsResponse getBankById(Long id) {
        UserBankDetails bank = repository.findById(id)
                .orElseThrow(() -> new BankNotFoundException("Bank details not found"));
        return mapper.toResponse(bank);
    }

    public Page<UserBankDetailsResponse> getAllBanks(
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    public Page<UserBankDetailsResponse> searchByBankName(
            String bankName,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByBankNameContainingIgnoreCase(
                        bankName,
                        pageable)
                .map(mapper::toResponse);
    }

    public Page<UserBankDetailsResponse> searchByIfsc(
            String ifsc,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByIfscCodeContainingIgnoreCase(
                        ifsc,
                        pageable)
                .map(mapper::toResponse);
    }

    public Page<UserBankDetailsResponse> searchByAccountType(
            AccountType accountType,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByAccountType(accountType, pageable)
                .map(mapper::toResponse);
    }

    public UserBankDetailsResponse searchByAccountNumber(String accountNumber) {
        return repository.findByAccountNumber(accountNumber)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BankNotFoundException("Bank details not found for account number: " + accountNumber));
    }
}
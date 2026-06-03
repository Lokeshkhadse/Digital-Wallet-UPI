package com.example.Query_Service.service;

import com.example.Query_Service.dto.UserBankBalanceResponse;
import com.example.Query_Service.exception.BankBalanceNotFound;
import com.example.Query_Service.mapper.UserBankBalanceMapper;
import com.example.Query_Service.repository.UserBankBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class UserBankBalanceService {

    private final UserBankBalanceRepository repository;
    private final UserBankBalanceMapper mapper;

    public UserBankBalanceResponse getBalanceByUserId(Long userId) {
        return repository.findByUserId(userId)
                .map(balance -> mapper.toResponse(balance))
                .orElseThrow(() -> new BankBalanceNotFound("Balance not found for user ID: " + userId));
    }

    public UserBankBalanceResponse getBalanceByBankId(Long userBankId) {
        return repository.findByUserBankId(userBankId)
                .map(balance -> mapper.toResponse(balance))
                .orElseThrow(() -> new BankBalanceNotFound("Balance not found for bank ID: " + userBankId));
    }


        public Page<UserBankBalanceResponse> getAllBankBalances(int page, int size) {

            Pageable pageable = PageRequest.of(page, size);

            return repository.findAll(pageable)
                    .map(mapper::toResponse);
        }

    public Page<UserBankBalanceResponse> getLowBalanceAccounts(
            BigDecimal threshold,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return repository
                .findByAvailableBalanceLessThanEqual(
                        threshold,
                        pageable)
                .map(mapper::toResponse);
    }
}

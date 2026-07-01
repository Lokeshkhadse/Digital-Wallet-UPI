package com.example.Action_Service.service;


import com.example.Action_Service.dto.UserBankBalanceRequest;
import com.example.Action_Service.dto.UserBankBalanceResponse;
import com.example.Action_Service.entity.UserBankBalance;
import com.example.Action_Service.entity.UserBankDetails;
import com.example.Action_Service.exception.DuplicateResourceException;
import com.example.Action_Service.exception.ResourceNotFoundException;
import com.example.Action_Service.mapper.UserBankBalanceMapper;
import com.example.Action_Service.repository.BankRepository;
import com.example.Action_Service.repository.UserBankBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserBankBalanceService {

    private final UserBankBalanceRepository balanceRepository;
    private final BankRepository bankRepository;
    private final UserBankBalanceMapper userBankBalanceMapper;

    @Transactional
    public UserBankBalanceResponse createBalance(
            UserBankBalanceRequest request) {

        log.info(
                "Balance creation initiated | UserId={} | BankId={}",
                request.getUserId(),
                request.getUserBankId());

        UserBankDetails bankDetails =
                bankRepository.findById(request.getUserBankId())
                        .orElseThrow(() -> {

                            log.warn(
                                    "Bank account not found | BankId={}",
                                    request.getUserBankId());

                            return new ResourceNotFoundException(
                                    "Bank account not found");
                        });

        if (!bankDetails.getUserId().equals(request.getUserId())) {

            log.warn(
                    "User and bank account mismatch | UserId={} | BankOwnerId={} | BankId={}",
                    request.getUserId(),
                    bankDetails.getUserId(),
                    request.getUserBankId());

            throw new ResourceNotFoundException(
                    "User and Bank account mismatch");
        }

        if (balanceRepository.existsByUserBankId(
                request.getUserBankId())) {

            log.warn(
                    "Balance already exists | BankId={}",
                    request.getUserBankId());

            throw new DuplicateResourceException(
                    "Balance already exists for this bank account");
        }

        UserBankBalance balance =
                userBankBalanceMapper.toEntity(request);

        UserBankBalance savedBalance =
                balanceRepository.save(balance);

        log.info(
                "Balance created successfully | BalanceId={} | BankId={}",
                savedBalance.getUserBankBalanceId(),
                savedBalance.getUserBankId());

        return userBankBalanceMapper.toResponse(savedBalance);
    }

    @Transactional
    public UserBankBalanceResponse updateBalance(
            Long userBankBalanceId,
            UserBankBalanceRequest request) {

        log.info(
                "Balance update initiated | BalanceId={} | UserId={}",
                userBankBalanceId,
                request.getUserId());

        UserBankBalance existing =
                balanceRepository.findByUserBankId(userBankBalanceId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Balance not found | BankId={}",
                                    userBankBalanceId);

                            return new ResourceNotFoundException(
                                    "Balance not found");
                        });

        if (!existing.getUserId().equals(request.getUserId())) {

            log.warn(
                    "User mismatch while updating balance | RequestedUserId={} | ActualUserId={}",
                    request.getUserId(),
                    existing.getUserId());

            throw new ResourceNotFoundException(
                    "User mismatch for this balance");
        }

        existing.setAvailableBalance(
                request.getAvailableBalance());

        UserBankBalance updated =
                balanceRepository.save(existing);

        log.info(
                "Balance updated successfully | BalanceId={} | NewBalance={}",
                updated.getUserBankBalanceId(),
                updated.getAvailableBalance());

        return userBankBalanceMapper.toResponse(updated);
    }

    @Transactional
    public String deleteBalance(
            Long userBankBalanceId) {

        log.info(
                "Balance deletion initiated | BalanceId={}",
                userBankBalanceId);

        UserBankBalance balance =
                balanceRepository.findById(userBankBalanceId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Balance record not found | BalanceId={}",
                                    userBankBalanceId);

                            return new ResourceNotFoundException(
                                    "Balance record not found");
                        });

        balanceRepository.delete(balance);

        log.info(
                "Balance deleted successfully | BalanceId={}",
                userBankBalanceId);

        return "Balance deleted successfully";
    }


}
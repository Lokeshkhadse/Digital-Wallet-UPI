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

@Service
@RequiredArgsConstructor
public class UserBankBalanceService {

    private final UserBankBalanceRepository balanceRepository;
    private final BankRepository bankRepository;
    private final UserBankBalanceMapper userBankBalanceMapper;



    @Transactional
    public UserBankBalanceResponse  createBalance(
            UserBankBalanceRequest request) {

        // Check bank exists
        UserBankDetails bankDetails =
                bankRepository.findById(request.getUserBankId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Bank account not found"));

        // User validation
        if (!bankDetails.getUserId().equals(request.getUserId())) {

            throw new ResourceNotFoundException(
                    "User and Bank account mismatch");
        }

        //  Balance already exists check
        if (balanceRepository.existsByUserBankId(
                request.getUserBankId())) {

            throw new DuplicateResourceException(
                    "Balance already exists for this bank account");
        }

        //  DTO -> Entity
        UserBankBalance balance =
                userBankBalanceMapper.toEntity(request);

        UserBankBalance savedBalance =
                balanceRepository.save(balance);

        // Entity -> DTO
        return userBankBalanceMapper.toResponse(savedBalance);
    }

    @Transactional
    public UserBankBalanceResponse updateBalance(Long userBankBalanceId, UserBankBalanceRequest request) {

        UserBankBalance existing =
                balanceRepository.findByUserBankId(userBankBalanceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Balance not found"));

        // optional validation
        if (!existing.getUserId().equals(request.getUserId())) {
            throw new ResourceNotFoundException("User mismatch for this balance");
        }

        existing.setAvailableBalance(request.getAvailableBalance());

        UserBankBalance updated = balanceRepository.save(existing);

        return userBankBalanceMapper.toResponse(updated);
    }

    @Transactional
    public String deleteBalance(Long userBankBalanceId) {

        UserBankBalance balance =
                balanceRepository.findById(userBankBalanceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Balance record not found"));

        balanceRepository.delete(balance);

        return "Balance deleted successfully";
    }


}
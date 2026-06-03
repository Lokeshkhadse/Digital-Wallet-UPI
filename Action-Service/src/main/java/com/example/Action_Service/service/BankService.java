package com.example.Action_Service.service;

import com.example.Action_Service.dto.BankRequest;
import com.example.Action_Service.dto.BankResponse;
import com.example.Action_Service.dto.UserResponseDto;
import com.example.Action_Service.entity.UserBankDetails;
import com.example.Action_Service.exception.BankNotFoundException;
import com.example.Action_Service.exception.DuplicateResourceException;
import com.example.Action_Service.exception.UserNotFoundException;
import com.example.Action_Service.feign.AuthServiceClient;
import com.example.Action_Service.mapper.BankMapper;
import com.example.Action_Service.repository.BankRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import feign.FeignException;



@Service
@AllArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final AuthServiceClient authServiceClient;
    private final BankMapper bankMapper;

    public BankResponse addBankDetails(BankRequest bankRequest) {



            UserResponseDto user =
                    authServiceClient.getUserById(bankRequest.getUserId());

            if (user == null) {
                throw new UserNotFoundException(
                        "User with id " + bankRequest.getUserId() + " not found"
                );
            }

            // Check duplicates
            if (bankRepository.existsByAccountNumber(bankRequest.getAccountNumber())) {
                throw new DuplicateResourceException("Account number already exists");
            }

            if (bankRequest.getUpiId() != null &&
                    bankRepository.existsByUpiId(bankRequest.getUpiId())) {
                throw new DuplicateResourceException("UPI ID already exists");
            }


            UserBankDetails userBankDetails =
                    bankMapper.toEntity(bankRequest);

            UserBankDetails savedDetails =
                    bankRepository.save(userBankDetails);

            return bankMapper.toResponse(savedDetails);


    }

    public String deleteBank(Long bankId) {

        if (!bankRepository.existsById(bankId)) {
            throw new BankNotFoundException("BankID not found");
        }

        bankRepository.deleteById(bankId);

        return "Bank deleted successfully";
    }
}

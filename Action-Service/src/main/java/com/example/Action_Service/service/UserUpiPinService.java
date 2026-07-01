package com.example.Action_Service.service;

import com.example.Action_Service.dto.ChangePinRequest;
import com.example.Action_Service.dto.CreatePinRequest;
import com.example.Action_Service.entity.UserUpiPin;
import com.example.Action_Service.exception.DuplicateResourceException;
import com.example.Action_Service.exception.ResourceNotFoundException;
import com.example.Action_Service.exception.ValidationException;
import com.example.Action_Service.repository.UserUpiPinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUpiPinService {

    private final UserUpiPinRepository pinRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String createPin(CreatePinRequest request) {
        log.info("UPI PIN creation initiated | UserId={} | BankId={}", request.getUserId(), request.getUserBankId());

        if (pinRepository.findByUserIdAndUserBankId(request.getUserId(), request.getUserBankId()).isPresent()) {
            log.warn("UPI PIN already exists | UserId={} | BankId={}", request.getUserId(), request.getUserBankId());
            throw new DuplicateResourceException("UPI PIN already exists");
        }

        UserUpiPin pin = UserUpiPin.builder()
                .userId(request.getUserId())
                .userBankId(request.getUserBankId())
                .pin(passwordEncoder.encode(request.getPin()))
                .active(true)
                .build();

        pinRepository.save(pin);
        log.info("UPI PIN created successfully | UserId={} | BankId={}", request.getUserId(), request.getUserBankId());
        return "UPI PIN created successfully";
    }


        public void validatePin(
            Long userId,
            Long userBankId,
            String enteredPin) {

        log.info(
                "UPI PIN validation initiated | UserId={} | BankId={}",
                userId,
                userBankId);

        UserUpiPin pin =
                pinRepository
                        .findByUserIdAndUserBankId(
                                userId,
                                userBankId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "UPI PIN not found | UserId={} | BankId={}",
                                    userId,
                                    userBankId);

                            return new ResourceNotFoundException(
                                    "UPI PIN not found");
                        });

        if (!passwordEncoder.matches(
                enteredPin,
                pin.getPin())) {

            log.warn(
                    "Invalid UPI PIN attempt | UserId={} | BankId={}",
                    userId,
                    userBankId);

            throw new ValidationException(
                    "Invalid UPI PIN");
        }

        log.info(
                "UPI PIN validated successfully | UserId={} | BankId={}",
                userId,
                userBankId);
    }


    @Transactional
    public String changePin(ChangePinRequest request) {
        log.info("UPI PIN change initiated | UserId={} | BankId={}", request.getUserId(), request.getUserBankId());

        UserUpiPin pin = pinRepository
                .findByUserIdAndUserBankId(request.getUserId(), request.getUserBankId())
                .orElseThrow(() -> {
                    log.warn("UPI PIN not found | UserId={} | BankId={}", request.getUserId(), request.getUserBankId());
                    return new ResourceNotFoundException("UPI PIN not found");
                });

        if (!passwordEncoder.matches(request.getOldPin(), pin.getPin())) {
            log.warn("Invalid Old UPI PIN during change request | UserId={}", request.getUserId());
            throw new ValidationException("Invalid old UPI PIN");
        }

        pin.setPin(passwordEncoder.encode(request.getNewPin()));

        pinRepository.save(pin);
        log.info("UPI PIN changed successfully | UserId={} | BankId={}", request.getUserId(), request.getUserBankId());
        return "UPI PIN changed successfully";
    }
}

package com.example.Action_Service.service;

import com.example.Action_Service.dto.ChangePinRequest;
import com.example.Action_Service.dto.CreatePinRequest;
import com.example.Action_Service.entity.UserUpiPin;
import com.example.Action_Service.exception.DuplicateResourceException;
import com.example.Action_Service.exception.ResourceNotFoundException;
import com.example.Action_Service.exception.ValidationException;
import com.example.Action_Service.repository.UserUpiPinRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUpiPinService {

    private final UserUpiPinRepository pinRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String createPin(CreatePinRequest request) {

        if(pinRepository.findByUserIdAndUserBankId(
                request.getUserId(),
                request.getUserBankId()).isPresent()) {

            throw new DuplicateResourceException(
                    "UPI PIN already exists");
        }

        UserUpiPin pin =
                UserUpiPin.builder()
                        .userId(request.getUserId())
                        .userBankId(request.getUserBankId())
                        .pin(passwordEncoder.encode(
                                request.getPin()))
                        .active(true)
                        .build();

        pinRepository.save(pin);

        return "UPI PIN created successfully";
    }

    public void validatePin(
            Long userId,
            Long userBankId,
            String enteredPin) {

        UserUpiPin pin =
                pinRepository
                        .findByUserIdAndUserBankId(
                                userId,
                                userBankId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "UPI PIN not found"));

        if(!passwordEncoder.matches(
                enteredPin,
                pin.getPin())) {

            throw new ValidationException(
                    "Invalid UPI PIN");
        }
    }

    @Transactional
    public String changePin(ChangePinRequest request) {

        UserUpiPin pin = pinRepository
                .findByUserIdAndUserBankId(
                        request.getUserId(),
                        request.getUserBankId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("UPI PIN not found"));

        // verify old PIN
        if (!passwordEncoder.matches(request.getOldPin(), pin.getPin())) {
            throw new ValidationException("Old PIN is incorrect");
        }

        // prevent same PIN reuse
        if (passwordEncoder.matches(request.getNewPin(), pin.getPin())) {
            throw new ValidationException("New PIN cannot be same as old PIN");
        }

        // update PIN
        pin.setPin(passwordEncoder.encode(request.getNewPin()));

        pinRepository.save(pin);

        return "UPI PIN changed successfully";
    }
}
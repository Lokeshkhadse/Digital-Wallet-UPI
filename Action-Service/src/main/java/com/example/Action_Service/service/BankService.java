package com.example.Action_Service.service;

import com.example.Action_Service.dto.BankRequest;
import com.example.Action_Service.dto.BankResponse;
import com.example.Action_Service.dto.UserResponseDto;
import com.example.Action_Service.entity.UserBankDetails;
import com.example.Action_Service.exception.AuthServiceUnavailableException;
import com.example.Action_Service.exception.BankNotFoundException;
import com.example.Action_Service.exception.DuplicateResourceException;
import com.example.Action_Service.exception.UserNotFoundException;
import com.example.Action_Service.feign.AuthServiceClient;
import com.example.Action_Service.mapper.BankMapper;
import com.example.Action_Service.rabbitmq.NotificationEvent;
import com.example.Action_Service.repository.BankRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Service
@AllArgsConstructor
@Slf4j
public class BankService {

    private final BankRepository bankRepository;
    private final AuthServiceClient authServiceClient;
    private final BankMapper bankMapper;
    private final EmailService emailService;
    private final QrPaymentService qrPaymentService;
    private final NotificationOutboxService outboxService;


//    @Transactional
//    public BankResponse addBankDetails(BankRequest bankRequest) {
//
//        UserResponseDto user;
//
//        try{
//            user = authServiceClient.getUserById(bankRequest.getUserId());
//        }catch (AuthServiceUnavailableException ex){
//            throw  ex;
//        }
//
//        if (user == null) {
//            throw new UserNotFoundException(
//                    "User with id " + bankRequest.getUserId() + " not found"
//            );
//        }
//
//            // Check duplicates
//        if (bankRepository.existsByAccountNumber(bankRequest.getAccountNumber())) {
//                throw new DuplicateResourceException("Account number already exists");
//        }
//
//        if (bankRequest.getUpiId() != null &&
//                    bankRepository.existsByUpiId(bankRequest.getUpiId())) {
//                throw new DuplicateResourceException("UPI ID already exists");
//        }
//
//
//        UserBankDetails userBankDetails =
//                    bankMapper.toEntity(bankRequest);
//
//        UserBankDetails savedDetails =
//                    bankRepository.save(userBankDetails);
//
//        //adding qr also
//        qrPaymentService.generateQr(savedDetails);
//
//       //email part
//
//        outboxService.saveNotification(
//                NotificationEvent.builder()
//                        .to(user.getEmail())
//                        .subject("Welcome to Digital Wallet - Account Created Successfully")
//                        .templateName("account-created")
//                        .variables(
//                                Map.of("name", user.getName(),
//                                        "accountNumber", savedDetails.getAccountNumber(),
//                                        "bankName", savedDetails.getBankName(),
//                                        "upiId", savedDetails.getUpiId()
//                                )
//                        )
//                        .build()
//        );
//
//
//        return bankMapper.toResponse(savedDetails);
//
//
//    }

    @Transactional
    public BankResponse addBankDetails(
            BankRequest bankRequest) {

        log.info(
                "Bank account creation started for UserId : {}",
                bankRequest.getUserId());

        UserResponseDto user;

        try {

            log.debug(
                    "Fetching user details from Auth Service. UserId : {}",
                    bankRequest.getUserId());

            user = authServiceClient.getUserById(
                    bankRequest.getUserId());

        } catch (AuthServiceUnavailableException ex) {

            log.error(
                    "Auth Service unavailable while fetching UserId : {}",
                    bankRequest.getUserId(),
                    ex);

            throw ex;
        }

        if (user == null) {

            log.warn(
                    "User not found. UserId : {}",
                    bankRequest.getUserId());

            throw new UserNotFoundException(
                    "User with id "
                            + bankRequest.getUserId()
                            + " not found");
        }

        if (bankRepository.existsByAccountNumber(
                bankRequest.getAccountNumber())) {

            log.warn(
                    "Duplicate Account Number detected : {}",
                    bankRequest.getAccountNumber());

            throw new DuplicateResourceException(
                    "Account number already exists");
        }

        if (bankRequest.getUpiId() != null &&
                bankRepository.existsByUpiId(
                        bankRequest.getUpiId())) {

            log.warn(
                    "Duplicate UPI Id detected : {}",
                    bankRequest.getUpiId());

            throw new DuplicateResourceException(
                    "UPI ID already exists");
        }

        log.info(
                "Saving bank account for UserId : {}",
                bankRequest.getUserId());

        UserBankDetails entity =
                bankMapper.toEntity(bankRequest);

        UserBankDetails savedDetails =
                bankRepository.save(entity);

        log.info(
                "Bank account created successfully. BankId : {}, Account Number : {}",
                savedDetails.getId(),
                savedDetails.getAccountNumber());

        log.debug(
                "Generating QR for BankId : {}",
                savedDetails.getId());

        qrPaymentService.generateQr(savedDetails);

        log.info(
                "QR generated successfully. BankId : {}",
                savedDetails.getId());

        log.debug(
                "Saving notification into Outbox for UserId : {}",
                bankRequest.getUserId());

        outboxService.saveNotification(
                NotificationEvent.builder()
                        .to(user.getEmail())
                        .subject("Welcome to Digital Wallet - Account Created Successfully")
                        .templateName("account-created")
                        .variables(
                                Map.of(
                                        "name", user.getName(),
                                        "accountNumber", savedDetails.getAccountNumber(),
                                        "bankName", savedDetails.getBankName(),
                                        "upiId", savedDetails.getUpiId()
                                )
                        )
                        .build()
        );

        log.info(
                "Notification saved into Outbox successfully.");

        log.info(
                "Bank account creation completed successfully. BankId : {}",
                savedDetails.getId());

        return bankMapper.toResponse(savedDetails);
    }

    @Transactional
    public String deleteBank(Long bankId) {

        log.info(
                "Bank delete request received. BankId : {}",
                bankId);

        if (!bankRepository.existsById(bankId)) {

            log.warn(
                    "Delete failed. BankId not found : {}",
                    bankId);

            throw new BankNotFoundException(
                    "BankID not found");
        }

        log.info(
                "Deleting BankId : {}",
                bankId);

        bankRepository.deleteById(bankId);

        log.info(
                "Bank deleted successfully. BankId : {}",
                bankId);

        return "Bank deleted successfully";
    }
}



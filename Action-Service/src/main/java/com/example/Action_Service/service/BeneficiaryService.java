
package com.example.Action_Service.service;

import com.example.Action_Service.dto.BeneficiaryRequest;
import com.example.Action_Service.dto.BeneficiaryResponse;
import com.example.Action_Service.dto.UserResponseDto;
import com.example.Action_Service.entity.Beneficiary;
import com.example.Action_Service.exception.BeneficiaryAlreadyExistsException;
import com.example.Action_Service.exception.ResourceNotFoundException;
import com.example.Action_Service.feign.AuthServiceClient;
import com.example.Action_Service.mapper.BeneficiaryMapper;
import com.example.Action_Service.repository.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository repository;
    private final BeneficiaryMapper mapper;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public BeneficiaryResponse addBeneficiary(
            BeneficiaryRequest request) {

        log.info("Adding beneficiary. UserId={}, BeneficiaryUserId={}",
                request.getUserId(),
                request.getBeneficiaryUserId());

        if (request.getUserId().equals(request.getBeneficiaryUserId())) {

            log.warn("User {} tried to add himself as beneficiary",
                    request.getUserId());

            throw new IllegalArgumentException(
                    "User cannot add himself as beneficiary");
        }

        log.debug("Fetching user details for UserId={}",
                request.getUserId());

        UserResponseDto user =
                authServiceClient.getUserById(
                        request.getUserId());

        if (user == null) {

            log.error("User not found. UserId={}",
                    request.getUserId());

            throw new ResourceNotFoundException(
                    "User not found");
        }

        log.debug("Fetching beneficiary details for BeneficiaryUserId={}",
                request.getBeneficiaryUserId());

        UserResponseDto beneficiaryUser =
                authServiceClient.getUserById(
                        request.getBeneficiaryUserId());

        if (beneficiaryUser == null) {

            log.error("Beneficiary user not found. BeneficiaryUserId={}",
                    request.getBeneficiaryUserId());

            throw new ResourceNotFoundException(
                    "Beneficiary user not found");
        }

        boolean exists =
                repository.existsByUserIdAndBeneficiaryUserId(
                        request.getUserId(),
                        request.getBeneficiaryUserId());

        if (exists) {

            log.warn("Beneficiary already exists. UserId={}, BeneficiaryUserId={}",
                    request.getUserId(),
                    request.getBeneficiaryUserId());

            throw new BeneficiaryAlreadyExistsException(
                    "Beneficiary already exists");
        }

        Beneficiary beneficiary =
                mapper.toEntity(request);

        Beneficiary saved =
                repository.save(beneficiary);

        log.info("Beneficiary added successfully. UserId={}",
                saved.getUserId());

        return mapper.toResponse(saved);
    }

    @Transactional
    public BeneficiaryResponse updateBeneficiary(
            Long beneficiaryId,
            BeneficiaryRequest request) {

        log.info("Updating beneficiary. BeneficiaryId={}",
                beneficiaryId);

        Beneficiary beneficiary =
                repository.findById(beneficiaryId)
                        .orElseThrow(() -> {

                            log.error("Beneficiary not found. BeneficiaryId={}",
                                    beneficiaryId);

                            return new ResourceNotFoundException(
                                    "Beneficiary not found");
                        });

        beneficiary.setNickname(
                request.getNickname());

        Beneficiary updated =
                repository.save(beneficiary);

        log.info("Beneficiary updated successfully");
        return mapper.toResponse(updated);
    }

    @Transactional
    public String deleteBeneficiary(
            Long beneficiaryId) {

        log.info("Deleting beneficiary. BeneficiaryId={}",
                beneficiaryId);

        Beneficiary beneficiary =
                repository.findById(beneficiaryId)
                        .orElseThrow(() -> {

                            log.error("Beneficiary not found. BeneficiaryId={}",
                                    beneficiaryId);

                            return new ResourceNotFoundException(
                                    "Beneficiary not found");
                        });

        repository.delete(beneficiary);

        log.info("Beneficiary deleted successfully. BeneficiaryId={}",
                beneficiaryId);

        return "Beneficiary deleted successfully";
    }
}
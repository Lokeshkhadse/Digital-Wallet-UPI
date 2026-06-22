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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository repository;
    private final BeneficiaryMapper mapper;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public BeneficiaryResponse addBeneficiary(
            BeneficiaryRequest request) {

        if(request.getUserId()
                .equals(request.getBeneficiaryUserId())) {

            throw new IllegalArgumentException(
                    "User cannot add himself as beneficiary");
        }

        UserResponseDto user =
                authServiceClient.getUserById(
                        request.getUserId());

        if(user == null) {

            throw new ResourceNotFoundException(
                    "User not found");
        }

        UserResponseDto beneficiaryUser =
                authServiceClient.getUserById(
                        request.getBeneficiaryUserId());

        if(beneficiaryUser == null) {

            throw new ResourceNotFoundException(
                    "Beneficiary user not found");
        }

        boolean exists =
                repository.existsByUserIdAndBeneficiaryUserId(
                        request.getUserId(),
                        request.getBeneficiaryUserId());

        if(exists) {

            throw new BeneficiaryAlreadyExistsException(
                    "Beneficiary already exists");
        }

        Beneficiary beneficiary =
                mapper.toEntity(request);

        Beneficiary saved =
                repository.save(beneficiary);

        return mapper.toResponse(saved);
    }

    @Transactional
    public BeneficiaryResponse updateBeneficiary(
            Long beneficiaryId,
            BeneficiaryRequest request) {

        Beneficiary beneficiary =
                repository.findById(beneficiaryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Beneficiary not found"));

        beneficiary.setNickname(
                request.getNickname());

        Beneficiary updated =
                repository.save(beneficiary);

        return mapper.toResponse(updated);
    }

    @Transactional
    public String deleteBeneficiary(
            Long beneficiaryId) {

        Beneficiary beneficiary =
                repository.findById(beneficiaryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Beneficiary not found"));

        repository.delete(beneficiary);

        return "Beneficiary deleted successfully";
    }
}
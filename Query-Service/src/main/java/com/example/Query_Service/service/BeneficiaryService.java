package com.example.Query_Service.service;

import com.example.Query_Service.dto.BeneficiaryResponse;
import com.example.Query_Service.entity.Beneficiary;
import com.example.Query_Service.exception.ResourceNotFoundException;
import com.example.Query_Service.mapper.BeneficiaryMapper;
import com.example.Query_Service.repository.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {
    private final BeneficiaryRepository repository;
    private final BeneficiaryMapper mapper;

    public List<BeneficiaryResponse>
    getUserBeneficiaries(Long userId) {

        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public BeneficiaryResponse
    getBeneficiary(Long beneficiaryId) {

        Beneficiary beneficiary =
                repository.findById(beneficiaryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Beneficiary not found"));

        return mapper.toResponse(beneficiary);
    }
}

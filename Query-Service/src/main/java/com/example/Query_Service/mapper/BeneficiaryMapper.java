package com.example.Query_Service.mapper;


import com.example.Query_Service.dto.BeneficiaryRequest;
import com.example.Query_Service.dto.BeneficiaryResponse;
import com.example.Query_Service.entity.Beneficiary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BeneficiaryMapper {

    public Beneficiary toEntity(
            BeneficiaryRequest request) {

        return Beneficiary.builder()
                .userId(request.getUserId())
                .beneficiaryUserId(
                        request.getBeneficiaryUserId())
                .nickname(request.getNickname())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public BeneficiaryResponse toResponse(
            Beneficiary beneficiary) {

        return BeneficiaryResponse.builder()
                .id(beneficiary.getId())
                .userId(beneficiary.getUserId())
                .beneficiaryUserId(
                        beneficiary.getBeneficiaryUserId())
                .nickname(beneficiary.getNickname())
                .createdAt(
                        beneficiary.getCreatedAt())
                .build();
    }
}
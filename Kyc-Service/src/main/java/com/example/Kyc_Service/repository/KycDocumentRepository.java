package com.example.Kyc_Service.repository;


import com.example.Kyc_Service.entity.KycDocument;
import com.example.Kyc_Service.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {

    List<KycDocument> findByKycId(Long kycId);

    Optional<KycDocument> findByKycIdAndDocumentType(
            Long kycId,
            DocumentType documentType
    );

    boolean existsByExtractedDocumentNumber(String extractedDocumentNumber);
}
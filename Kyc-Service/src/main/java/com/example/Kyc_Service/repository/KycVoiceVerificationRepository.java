package com.example.Kyc_Service.repository;

import com.example.Kyc_Service.entity.KycVoiceVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycVoiceVerificationRepository
        extends JpaRepository<KycVoiceVerification, Long> {

    Optional<KycVoiceVerification> findByUserId(Long userId);

    Optional<KycVoiceVerification> findByKycId(Long kycId);
}
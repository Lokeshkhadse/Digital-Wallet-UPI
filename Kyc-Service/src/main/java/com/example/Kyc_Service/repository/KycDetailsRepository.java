package com.example.Kyc_Service.repository;


import com.example.Kyc_Service.entity.KycDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycDetailsRepository extends JpaRepository<KycDetails, Long> {

    Optional<KycDetails> findByUserId(Long userId);

}
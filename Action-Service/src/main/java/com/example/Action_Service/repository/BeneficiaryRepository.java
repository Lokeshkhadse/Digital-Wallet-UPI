package com.example.Action_Service.repository;

import com.example.Action_Service.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByUserId(Long userId);

    boolean existsByUserIdAndBeneficiaryUserId(
            Long userId,
            Long beneficiaryUserId
    );
}

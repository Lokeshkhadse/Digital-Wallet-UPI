package com.example.Query_Service.repository;

import com.example.Query_Service.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByUserId(Long userId);
}

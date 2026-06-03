package com.example.Query_Service.repository;

import com.example.Query_Service.entity.UserBankDetails;
import com.example.Query_Service.enums.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBankDetailsRepository
        extends JpaRepository<UserBankDetails, Long> {

    List<UserBankDetails> findByUserId(Long userId);

    Page<UserBankDetails> findByBankNameContainingIgnoreCase(
            String bankName,
            Pageable pageable);

    Page<UserBankDetails> findByIfscCodeContainingIgnoreCase(
            String ifscCode,
            Pageable pageable);

    Page<UserBankDetails> findByAccountType(
            AccountType accountType,
            Pageable pageable);

    Optional<UserBankDetails> findByAccountNumber(String accountNumber);
}
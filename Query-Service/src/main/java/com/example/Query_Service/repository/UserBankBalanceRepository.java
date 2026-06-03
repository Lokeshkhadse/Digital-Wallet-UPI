package com.example.Query_Service.repository;

import com.example.Query_Service.entity.UserBankBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface UserBankBalanceRepository extends JpaRepository<UserBankBalance,Long> {

    Optional<UserBankBalance> findByUserBankId(Long bankId);


    Optional<UserBankBalance> findByUserId(Long userId);

    Page<UserBankBalance> findByAvailableBalanceLessThanEqual(BigDecimal threshold, Pageable pageable);
}

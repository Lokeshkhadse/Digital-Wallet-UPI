package com.example.Action_Service.repository;

import com.example.Action_Service.dto.UserBankBalanceRequest;
import com.example.Action_Service.entity.UserBankBalance;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBankBalanceRepository extends JpaRepository<UserBankBalance,Long> {
    boolean existsByUserBankId(@NotNull Long userBankId);

    Optional<UserBankBalance> findByUserBankId(Long bankId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b
            FROM UserBankBalance b
            WHERE b.userBankId = :userBankId
           """)
    Optional<UserBankBalance> lockByUserBankId(
            @Param("userBankId") Long userBankId);
}

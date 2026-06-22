package com.example.Action_Service.repository;

import com.example.Action_Service.entity.UserUpiPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserUpiPinRepository
        extends JpaRepository<UserUpiPin, Long> {

    Optional<UserUpiPin> findByUserIdAndUserBankId(
            Long userId,
            Long userBankId);

}
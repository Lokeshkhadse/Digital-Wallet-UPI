package com.example.Query_Service.repository;

import com.example.Query_Service.entity.QrPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QrPaymentRepository
        extends JpaRepository<QrPayment,Long> {

    Optional<QrPayment> findByUpiId(String upiId);

    Optional<QrPayment> findByUserBankId(Long userBankId);
}
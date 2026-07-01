package com.example.Action_Service.repository;

import com.example.Action_Service.entity.QrPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrPaymentRepository
        extends JpaRepository<QrPayment,Long> {

    Optional<QrPayment> findByUpiId(String upiId);

    boolean existsByUpiId(String upiId);
}

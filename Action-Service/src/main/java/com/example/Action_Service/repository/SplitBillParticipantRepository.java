package com.example.Action_Service.repository;

import com.example.Action_Service.entity.SplitBillParticipant;
import com.example.Action_Service.enums.SplitPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SplitBillParticipantRepository
        extends JpaRepository<SplitBillParticipant, Long> {

    List<SplitBillParticipant> findBySplitBillId(
            Long splitBillId);

    List<SplitBillParticipant> findByUserId(
            Long userId);

    Optional<SplitBillParticipant>
    findBySplitBillIdAndUserId(
            Long splitBillId,
            Long userId);

    long countBySplitBillIdAndPaymentStatus(
            Long splitBillId,
            SplitPaymentStatus status);



    long countBySplitBillId(
            Long splitBillId);

    List<SplitBillParticipant>
    findByUserIdAndPaymentStatus(
            Long userId,
            SplitPaymentStatus paymentStatus);
}
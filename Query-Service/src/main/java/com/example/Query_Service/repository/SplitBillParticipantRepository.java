package com.example.Query_Service.repository;

import com.example.Query_Service.entity.SplitBillParticipant;
import com.example.Query_Service.enums.SplitPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SplitBillParticipantRepository
        extends JpaRepository<
                SplitBillParticipant,
                Long> {

    List<SplitBillParticipant>
    findBySplitBillId(
            Long splitBillId);

    List<SplitBillParticipant>
    findByUserIdAndPaymentStatus(
            Long userId,
            SplitPaymentStatus status);

    long countBySplitBillId(
            Long splitBillId);

    long countBySplitBillIdAndPaymentStatus(
            Long splitBillId,
            SplitPaymentStatus status);

    List<SplitBillParticipant> findBySplitBillIdIn(List<Long> splitBillIds);
}

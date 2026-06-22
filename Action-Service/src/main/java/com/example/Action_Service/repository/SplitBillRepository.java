package com.example.Action_Service.repository;

import com.example.Action_Service.entity.SplitBill;
import com.example.Action_Service.enums.SplitBillStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SplitBillRepository
        extends JpaRepository<SplitBill, Long> {

    List<SplitBill> findByCreatedByUserId(
            Long createdByUserId);

    List<SplitBill> findByStatus(
            SplitBillStatus status);

    List<SplitBill> findByCreatedByUserIdOrderByCreatedAtDesc(
            Long userId);
}
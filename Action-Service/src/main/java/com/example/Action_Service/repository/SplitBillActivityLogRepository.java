package com.example.Action_Service.repository;

import com.example.Action_Service.entity.SplitBillActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SplitBillActivityLogRepository
        extends JpaRepository<SplitBillActivityLog, Long> {

    List<SplitBillActivityLog>
    findBySplitBillIdOrderByCreatedAtDesc(
            Long splitBillId);
}
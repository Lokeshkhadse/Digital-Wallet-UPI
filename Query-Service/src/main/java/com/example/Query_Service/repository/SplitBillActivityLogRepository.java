package com.example.Query_Service.repository;

import com.example.Query_Service.entity.SplitBillActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SplitBillActivityLogRepository
        extends JpaRepository<
                SplitBillActivityLog,
                Long> {

    List<SplitBillActivityLog>
    findBySplitBillIdOrderByCreatedAtDesc(
            Long splitBillId);
}
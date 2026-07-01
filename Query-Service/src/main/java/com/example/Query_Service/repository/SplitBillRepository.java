package com.example.Query_Service.repository;

import com.example.Query_Service.entity.SplitBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SplitBillRepository
        extends JpaRepository<SplitBill,Long> {

    List<SplitBill>
    findByCreatedByUserIdOrderByCreatedAtDesc(
            Long userId);

    List<SplitBill> findBySplitBillIdIn(List<Long> splitBillIds);
}
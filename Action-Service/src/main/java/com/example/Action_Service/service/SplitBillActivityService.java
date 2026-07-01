package com.example.Action_Service.service;

import com.example.Action_Service.entity.SplitBill;
import com.example.Action_Service.entity.SplitBillActivityLog;
import com.example.Action_Service.enums.ActivityType;
import com.example.Action_Service.repository.SplitBillActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SplitBillActivityService {

    private final SplitBillActivityLogRepository repository;

    public void log(
            SplitBill splitBill,
            ActivityType activityType,
            String remarks,
            Long userId) {

        SplitBillActivityLog log =
                SplitBillActivityLog.builder()
                        .splitBillId(
                                splitBill.getSplitBillId())
                        .userId(userId)
                        .activityType(activityType)
                        .remarks(remarks)
                        .createdAt(LocalDateTime.now())
                        .build();

        repository.save(log);
    }
}
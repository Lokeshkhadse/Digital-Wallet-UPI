package com.example.Action_Service.repository;

import com.example.Action_Service.entity.ScheduledTransfer;
import com.example.Action_Service.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledTransferRepository
        extends JpaRepository<ScheduledTransfer,Long> {

    List<ScheduledTransfer>
    findByStatusAndNextExecutionTimeLessThanEqual(
            ScheduleStatus status,
            LocalDateTime time
    );
}
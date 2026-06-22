package com.example.Action_Service.repository;

import com.example.Action_Service.entity.NotificationOutbox;
import com.example.Action_Service.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationOutboxRepository
        extends JpaRepository<NotificationOutbox, Long> {

    List<NotificationOutbox>
    findTop100ByStatusOrderByCreatedAtAsc(
            OutboxStatus status);

    List<NotificationOutbox>
    findTop100ByStatusInOrderByCreatedAtAsc(
            List<OutboxStatus> statuses);
}
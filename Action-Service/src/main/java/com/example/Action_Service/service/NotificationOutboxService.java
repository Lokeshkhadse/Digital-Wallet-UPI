package com.example.Action_Service.service;

import com.example.Action_Service.entity.NotificationOutbox;
import com.example.Action_Service.enums.OutboxStatus;
import com.example.Action_Service.rabbitmq.NotificationEvent;
import com.example.Action_Service.repository.NotificationOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationOutboxService {

    private final NotificationOutboxRepository repository;

    private final ObjectMapper objectMapper;

    public void saveNotification(
            NotificationEvent event) {

        try {

            String payload =
                    objectMapper
                            .writeValueAsString(
                                    event);

            NotificationOutbox outbox =
                    NotificationOutbox.builder()
                            .payload(payload)
                            .status(
                                    OutboxStatus.PENDING)
                            .retryCount(0)
                            .createdAt(
                                    LocalDateTime.now())
                            .build();

            repository.save(outbox);

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Unable to save notification",
                    ex);
        }
    }
}
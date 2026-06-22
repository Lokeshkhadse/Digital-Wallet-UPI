package com.example.Action_Service.util;

import com.example.Action_Service.entity.NotificationOutbox;
import com.example.Action_Service.enums.OutboxStatus;
import com.example.Action_Service.rabbitmq.NotificationEvent;
import com.example.Action_Service.rabbitmq.RabbitConfig;
import com.example.Action_Service.repository.NotificationOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationOutboxScheduler {

    private final NotificationOutboxRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 60000)
    public void processOutbox() {

        List<NotificationOutbox> events =
                repository.findTop100ByStatusOrderByCreatedAtAsc(
                        OutboxStatus.PENDING);

        for (NotificationOutbox outbox : events) {

            try {
                // mark processing first (avoid duplicate pickup)
                outbox.setStatus(OutboxStatus.PROCESSING);
                repository.save(outbox);

                NotificationEvent event =
                        objectMapper.readValue(
                                outbox.getPayload(),
                                NotificationEvent.class);

                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE,
                        RabbitConfig.ROUTING_KEY,
                        event
                );

                // SUCCESS
                outbox.setStatus(OutboxStatus.SUCCESS);
                repository.save(outbox);

            } catch (Exception ex) {

                log.error("Failed to publish outbox id {}", outbox.getId(), ex);

                int retry = outbox.getRetryCount() + 1;
                outbox.setRetryCount(retry);

                if (retry > 5) {
                    outbox.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox permanently failed id {}", outbox.getId());
                } else {
                    outbox.setStatus(OutboxStatus.PENDING);
                }

                repository.save(outbox);
            }
        }
    }

}
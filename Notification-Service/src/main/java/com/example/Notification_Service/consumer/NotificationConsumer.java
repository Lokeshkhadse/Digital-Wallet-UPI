package com.example.Notification_Service.consumer;

import com.example.Notification_Service.config.RabbitConfig;
import com.example.Notification_Service.dto.NotificationEvent;
import com.example.Notification_Service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @RabbitListener(
            queues = RabbitConfig.QUEUE)
    public void consume(
            NotificationEvent event) {

        log.info(
                "Notification Received : {}",
                event.getTo());

        emailService.sendHtmlEmail(event);
    }
}
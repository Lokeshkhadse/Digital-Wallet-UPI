package com.example.Notification_Service.service;

import com.example.Notification_Service.dto.NotificationEvent;
import com.example.Notification_Service.exception.FailedToSendEmail;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendHtmlEmail(NotificationEvent event) {

        try {

            Context context = new Context();

            Map<String, Object> variables =
                    event.getVariables();

            if (variables != null &&
                    !variables.isEmpty()) {

                variables.forEach(
                        context::setVariable);
            }

            String html =
                    templateEngine.process(
                            event.getTemplateName(),
                            context);

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8");

            helper.setTo(
                    event.getTo());

            helper.setSubject(
                    event.getSubject());

            helper.setText(
                    html,
                    true);

            helper.setFrom(
                    new InternetAddress(
                            "lokeshkhadse.eidiko@gmail.com",
                            "Digital Wallet Bank"
                    )
            );

            mailSender.send(message);

            log.info(
                    "Email sent successfully to {}",
                    event.getTo());

        } catch (Exception ex) {

            log.error(
                    "Email sending failed for {}",
                    event.getTo(),
                    ex);

            throw new FailedToSendEmail(
                    "Failed to send email"
                    );
        }
    }
}
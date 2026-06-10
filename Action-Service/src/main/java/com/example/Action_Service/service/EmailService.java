package com.example.Action_Service.service;

import com.example.Action_Service.dto.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendHtmlEmail(
            EmailRequest request) {

        try {

            Context context =
                    new Context();

            request.getVariables()
                    .forEach(
                            context::setVariable);

            String html =
                    templateEngine.process(
                            request.getTemplateName(),
                            context);

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true);

            helper.setTo(
                    request.getTo());

            helper.setSubject(
                    request.getSubject());

            helper.setText(
                    html,
                    true);

            mailSender.send(message);

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to send email",
                    ex);
        }
    }
}
package com.example.Action_Service.service;

import com.example.Action_Service.dto.EmailRequest;
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

    public void sendHtmlEmail(EmailRequest request) {

        try {

            if (request == null) {
                throw new RuntimeException("EmailRequest is null");
            }

            Context context = new Context();

            Map<String, Object> variables = request.getVariables();

            if (variables != null && !variables.isEmpty()) {
                variables.forEach(context::setVariable);
            }

            String html =
                    templateEngine.process(
                            request.getTemplateName(),
                            context);

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(html, true);

            // Optional but recommended
            helper.setFrom(
                    new InternetAddress(
                            "lokeshkhadse.eidiko@gmail.com",
                            "Digital Wallet Bank"
                    )
            );

            mailSender.send(message);

            log.info("Email sent successfully to {}", request.getTo());

        } catch (Exception ex) {

            log.error("Email sending failed", ex);

            throw new RuntimeException(
                    "Failed to send email : " + ex.getMessage(),
                    ex
            );
        }
    }
}
package com.example.Notification_Service.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {

    private String to;

    private String subject;

    private String templateName;

    private Map<String,Object> variables;
}
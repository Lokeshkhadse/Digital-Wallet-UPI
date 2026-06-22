package com.example.Query_Service.entity;


import com.example.Query_Service.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "split_bill_activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitBillActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    private Long splitBillId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    private String remarks;

    private LocalDateTime createdAt;
}
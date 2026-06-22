package com.example.Query_Service.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qrId;

    private Long userId;

    private Long userBankId;

    private String upiId;

    private String qrContent;

    private Boolean active;

    private LocalDateTime createdAt;
}

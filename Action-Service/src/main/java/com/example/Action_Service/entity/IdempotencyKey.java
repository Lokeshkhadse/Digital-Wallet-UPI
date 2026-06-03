package com.example.Action_Service.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
@Data
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idempotencyId;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    private String transactionRefNo;

    @CreatedDate
    private LocalDateTime createdAt;
}
package com.example.Query_Service.entity;

//Real banking apps me jab tum kisi ko baar-baar paise bhejte ho, toh har baar account number
// enter nahi karte.

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "beneficiary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long beneficiaryUserId;

    private String nickname;

    private LocalDateTime createdAt;
}

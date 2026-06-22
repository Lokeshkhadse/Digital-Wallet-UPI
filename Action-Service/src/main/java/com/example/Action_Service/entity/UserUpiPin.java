package com.example.Action_Service.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user_upi_pin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpiPin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long userBankId;

    private String pin;

    private Boolean active;
}

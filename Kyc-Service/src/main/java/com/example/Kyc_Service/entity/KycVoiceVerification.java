package com.example.Kyc_Service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_voice_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycVoiceVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "kyc_id")
    private Long kycId;

    @Lob
    @Column(name = "audio_base64", columnDefinition = "LONGTEXT")
    private String audioBase64;

    @Lob
    @Column(name = "speech_text", columnDefinition = "LONGTEXT")
    private String speechText;

    @Column(name = "extracted_name")
    private String extractedName;

    @Column(name = "extracted_dob")
    private String extractedDob;

    @Column(name = "extracted_pan")
    private String extractedPan;

    @Column(name = "extracted_aadhaar")
    private String extractedAadhaar;

    @Column(name = "verification_status")
    private String verificationStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}